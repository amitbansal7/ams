package amitbansal.services


import amitbansal.controllers.requests.{AchievementActionReq, AchievementGetAllReq, AchievementGetAllUAReq, AchievementGetReq}
import amitbansal.models.{Achievement, User}
import amitbansal.repositories.{AchievementRepository, UserRepository}
import cats.data.OptionT
import cats.instances.future._
import com.google.inject.{Inject, Singleton}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AchievementService {

  case class AchievementServiceResponse(bool: Boolean, message: String)

  case class AchievementServiceResponseToken(bool: Boolean, data: Seq[Achievement])

}

@Singleton
class AchievementService @Inject()(userService: UserService, achievementRepository: AchievementRepository, awsS3Service: AwsS3Service, imageCompressionService: ImageCompressionService, userRepository: UserRepository) {

  import AchievementService._

  // /mnt/data/static
  val baseStaticPath = "/mnt/data/static/"

  def paginate(achs: Seq[Achievement], offset: Option[Int], limit: Option[Int]): Seq[Achievement] = {
    val sortedAchs = achs.sortWith(_.date > _.date)
    if (offset.isDefined && limit.isDefined) {
      sortedAchs.toList.drop(offset.get).take(limit.get)
    } else sortedAchs
  }

  def filterByfields(
    achs: Future[Seq[Achievement]],
    rollno: Option[String],
    department: Option[String],
    semester: Option[Int],
    dateFrom: Option[String],
    dateTo: Option[String],
    shift: Option[String],
    section: Option[String],
    sessionFrom: Option[String],
    sessionTo: Option[String],
    category: Option[String]
  ): Future[Seq[Achievement]] = {
    achs.map { achss =>
      for {
        a: Achievement <- achss;
        if ((!rollno.isDefined || (rollno.isDefined && rollno.get == a.rollNo)) &&
          (!semester.isDefined || (semester.isDefined && semester.get.equals(a.semester))) &&
          (!dateFrom.isDefined || (dateFrom.isDefined && dateFrom.get <= a.date)) &&
          (!dateTo.isDefined || (dateTo.isDefined && dateTo.get >= a.date)) &&
          (!shift.isDefined || (shift.isDefined && shift.get.equals(a.shift))) &&
          (!section.isDefined || (section.isDefined && section.get.equals(a.section))) &&
          (!sessionFrom.isDefined || (sessionFrom.isDefined && sessionFrom.get.equals(a.sessionFrom))) &&
          (!sessionTo.isDefined || (sessionTo.isDefined && sessionTo.get.equals(a.sessionTo))) &&
          (!category.isDefined || (category.isDefined && category.get.equals(a.category))))
      } yield a
    }
  }

  def getAllApproved(req: AchievementGetAllReq): Future[Seq[Achievement]] = {
    req.department.map { dept =>
      filterByfields(
        achievementRepository.findAllApprovedByDepartment(dept.toLowerCase),
        req.rollno, req.department, req.semester, req.dateFrom, req.dateTo, req.shift, req.section, req.sessionFrom, req.sessionTo, req.category
      )
    }.getOrElse {
      filterByfields(
        achievementRepository.findAllApproved(req.offset, req.limit),
        req.rollno, req.department, req.semester, req.dateFrom, req.dateTo, req.shift, req.section, req.sessionFrom, req.sessionTo, req.category
      )
    }.map {
      paginate(_, req.offset, req.limit)
    }
  }

  def toggleApproved(id: String, token: String, action: Boolean): Future[AchievementServiceResponse] = {
    val objId = Utils.checkObjectId(id)

    val ach: Future[Achievement] = achievementRepository.findById(objId.get)
    val user: Future[Option[User]] = userService.getUserFromToken(token)

    OptionT(user).map { u =>
      ach.map { a =>
        if (a.isInstanceOf[Achievement] && a.department == u.department && u.shift == a.shift) {
          if (action)
            achievementRepository.approveByUser(objId.get, u._id.toHexString)
          else achievementRepository.approve(objId.get, action)

          AchievementServiceResponse(true, "Done")
        } else {
          AchievementServiceResponse(false, "Access denied")
        }
      }
    }.getOrElse {
      Future(AchievementServiceResponse(false, "No user found"))
    }.flatMap(identity)
  }

  def approveAch(req: AchievementActionReq) = toggleApproved(req.id, req.token, true)

  def unApproveAch(req: AchievementActionReq) = toggleApproved(req.id, req.token, false)

  def deleteAch(req: AchievementActionReq): Future[AchievementServiceResponse] = {

    val objId = Utils.checkObjectId(req.id)

    val user: Future[Option[User]] = userService.getUserFromToken(req.token)
    val ach: Future[Achievement] = achievementRepository.findById(objId.get)

    OptionT(user).map { u =>
      ach.map { a =>
        if (a.isInstanceOf[Achievement] && a.department == u.department && a.shift == u.shift) {
          achievementRepository.deleteOne(objId.get)
          AchievementServiceResponse(true, "Done")
        } else {
          AchievementServiceResponse(false, "Access denied")
        }
      }
    }.getOrElse {
      Future(AchievementServiceResponse(false, "No user found"))
    }.flatMap(identity)
  }

  def getOne(req: AchievementGetReq) = {

    val objId = Utils.checkObjectId(req.id).get
    val ach = achievementRepository.findById(objId)

    ach.map {
      case a: Achievement if a.approved =>
        val user: Future[User] = userRepository.getById(Utils.checkObjectId(a.approvedBy.get).get)
        val res: Future[Option[Achievement]] = user.map { u =>
          Some(Achievement.apply(a, Some(u.email)))
        }
        res
      case a: Achievement => Future(Some(a))
      case _ => Future(None)
    }.flatMap(identity)
  }

  def getAllUnapproved(req: AchievementGetAllUAReq): Future[Future[AchievementServiceResponseToken]] = {
    OptionT(userService.getUserFromToken(req.token)).map { user =>
      val data = achievementRepository
        .findAllByUnApprovedDepartmentAndDepartment(user.department, user.shift)
        .map(d => filterByfields(
          Future(d), req.rollno, None, req.semester, req.dateFrom, req.dateTo, req.shift, req.section, req.sessionFrom, req.sessionTo, req.category)
        )
        .flatMap(identity)

      data.map(d => AchievementServiceResponseToken(true, paginate(d, req.offset, req.limit)))
    }.getOrElse {
      Future(AchievementServiceResponseToken(false, List()))
    }
  }

  //
  //  def addAchievement(
  //    title: String,
  //    rollNo: String,
  //    department: String,
  //    semester: Int,
  //    date: String,
  //    shift: String,
  //    section: String,
  //    sessionFrom: String,
  //    sessionTo: String,
  //    venue: String,
  //    category: String,
  //    participated: Boolean, //coordinated if false
  //    name: String,
  //    description: String,
  //    eventName: String,
  //    file: File,
  //    meta: FileInfo
  //  ): AchievementServiceResponse = {
  //
  //    if (!Achievement.shifts.contains(shift))
  //      return AchievementServiceResponse(false, "invalid shift")
  //
  //    if (!Achievement.sections.contains(section))
  //      return AchievementServiceResponse(false, "invalid section")
  //
  //    if (!Achievement.semester.contains(semester))
  //      return AchievementServiceResponse(false, "invalid semester")
  //
  //    if (!Achievement.departments.contains(department))
  //      return AchievementServiceResponse(false, "invalid department")
  //
  //    if (!Achievement.categories.contains(category))
  //      return AchievementServiceResponse(false, "invalid category")
  //
  //    if (!meta.contentType.toString().startsWith("image"))
  //      return AchievementServiceResponse(false, "Invalid file type")
  //
  //    val imageRes = imageCompressionService.processImage(file)
  //
  //    if (!imageRes.bool)
  //      return AchievementServiceResponse(false, imageRes.message)
  //
  //    val str = Random
  //      .alphanumeric
  //      .take(7).toList
  //      .foldLeft("")((acc, ch) => acc + ch)
  //
  //    val fileName = (str + meta.getFileName).replace(" ", "-")
  //    val outFile = new File(baseStaticPath + fileName)
  //
  //    val path = Paths.get(baseStaticPath + fileName)
  //    Files.write(path, imageRes.buffer)
  //
  //    //aws>>>>>
  //    //    val res = awsS3Service.uploadImage(path.toFile, fileName)
  //    //    if (!res)
  //    //      return AchievementServiceResponse(false, "Failed to upload image, try again later.")
  //
  //    file.delete()
  //
  //    achievementRepository
  //      .addAchievement(Achievement.apply(title, rollNo, department, semester, date, shift, section, sessionFrom, sessionTo, venue, category, participated, name, fileName, description, eventName))
  //
  //    AchievementServiceResponse(true, "Achievement successfully added")
  //  }
}
