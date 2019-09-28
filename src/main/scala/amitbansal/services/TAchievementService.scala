package amitbansal.services

import amitbansal.controllers.requests._
import amitbansal.models.{TAchievement, User}
import amitbansal.repositories.{TAchievementRepository, UserRepository}
import amitbansal.services.TAchievementService._
import amitbansal.services.UserService.UserData
import amitbansal.utils.Serializers.ObjectIdJsonSerializer
import cats.data.OptionT
import cats.implicits._
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.google.inject.{Inject, Singleton}
import org.mongodb.scala.bson.ObjectId

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object TAchievementService {

  case class TAchievementServiceResponse(bool: Boolean, message: String)

  case class TAchievementServiceData(bool: Boolean, user: Option[UserData], achs: Seq[TAchievement])

  case class TAchAggRes(user: UserData, data: Map[String, TAchLocations])

  case class TAchAllRes(@JsonSerialize(using = classOf[ObjectIdJsonSerializer]) id: ObjectId, email: String, firstName: String, lastName: String, department: String, shift: String, designation: String, achievements: Seq[TAchievement])

  case class TAchLocations(msi: TAchNatInt, others: TAchNatInt)

  case class TAchNatInt(int: Int, nat: Int)

}

@Singleton
class TAchievementService @Inject()(tAchievementRepository: TAchievementRepository, userService: UserService, userRepository: UserRepository) {

  def add(req: TAchievementAddReq): Future[TAchievementServiceResponse] = {
    OptionT(userService.getUserFromToken(req.token)).map { user =>
      tAchievementRepository.add(
        TAchievement(
          user._id, req.taType, req.subType, req.international, req.topic, req.published, req.sponsored, req.reviewed, req.date, req.description, req.msi, req.place
        )
      )
      TAchievementServiceResponse(true, "Successfully added.")
    }.getOrElse {
      TAchievementServiceResponse(false, "Access Denied")
    }
  }

  def update(req: TAchievementUpdateReq): Future[TAchievementServiceResponse] = {

    val objId = Utils.checkObjectId(req.id)

    val tAchFromId = tAchievementRepository.getOneById(objId.get)

    OptionT(userService.getUserFromToken(req.token)).map { user =>
      checkIfTAchBelongsToThisUser(tAchFromId, user) map {
        case true =>
          tAchievementRepository.update(
            objId.get,
            TAchievement(
              user._id, req.taType, req.subType, req.international, req.topic, req.published, req.sponsored, req.reviewed, req.date, req.description, req.msi, req.place
            )
          )
          TAchievementServiceResponse(true, "Successfully updated.")
        case false =>
          TAchievementServiceResponse(false, "Access Denied")
      }
    }.getOrElse {
      Future(TAchievementServiceResponse(false, "Access Denied"))
    }.flatMap(identity)

  }


  def getAllForUserId(req: TAchievementGetAllByUserIdReq): Future[TAchievementServiceData] = {
    Utils.checkObjectId(req.userId).map { objId =>
      val user = userRepository.getById(objId).map { u =>
        if (u != null)
          Some(UserData(u._id, u.email, u.firstName, u.lastName, u.department, u.shift, u.designation))
        else None
      }
      user.map(u => tAchievementRepository.getAllByUserId(objId).map(d => TAchievementServiceData(u.isDefined, u, d))).flatMap(identity)
    }.getOrElse {
      Future {
        TAchievementServiceData(false, None, List[TAchievement]())
      }
    }
  }

  def evalForOneUser(user: User, data: List[(String, Seq[TAchievement])]): TAchAggRes = {

    //(taType, TAchLocations)
    val mappedData = data.map {
      unit =>
        val (msi, others) = unit._2.partition(_.msi)
        val msiNatInt = msi.partition(_.international)
        val othersNatInt = others.partition(_.international)
        val msiLocations = TAchNatInt(msiNatInt._1.size, msiNatInt._2.size)
        val otherLocations = TAchNatInt(othersNatInt._1.size, othersNatInt._2.size)
        (unit._1, TAchLocations(msiLocations, otherLocations))
    }.toMap

    TAchAggRes(
      UserData(user._id, user.email, user.firstName, user.lastName, user.department, user.shift, user.designation),
      mappedData
    )
  }

  def getAll(req: TAchievementGetAllReq): Future[Seq[TAchAllRes]] = {

    val allAchsFuture: Future[Seq[TAchievement]] = tAchievementRepository.getAll()

    val allAchsGroupedByUserFuture = allAchsFuture.map {
      all =>
        all.flatMap {
          ach =>
            if ((!req.fromDate.isDefined || (ach.date >= req.fromDate.get)) &&
              (!req.toDate.isDefined || (ach.date <= req.toDate.get)) &&
              (!req.taType.isDefined || (ach.taType == req.taType.get))) List(ach)
            else List[TAchievement]()
        }
    }.map {
      all => all.groupBy(ach => ach.user)
    }

    val allUsersFuture = userRepository.getAllUsers()
    val allUsersFilteredByDeptFuture = allUsersFuture.map {
      users =>
        if (req.department.isDefined) users.filter(_.department == req.department.get)
        else users
    }

    val allUsersWithAchs = for {
      users <- allUsersFilteredByDeptFuture
      allAch <- allAchsGroupedByUserFuture
    } yield users.map {
      user =>
        TAchAllRes(user._id, user.email, user.firstName, user.lastName, user.department, user.shift, user.designation, allAch.getOrElse(user._id, List[TAchievement]()))
    }

    val allUsersWithAtleastOneAch = allUsersWithAchs.map {
      all => all.filter(_.achievements.size > 0)
    }

    allUsersWithAtleastOneAch
  }

  def getAllAggregated(req: TAchievementGetAllAggReq) = {

    val allUsers = userRepository
      .getAllUsers()

    val userIdToUserMap = allUsers.map(users => users.map {
      user =>
        (user._id, user)
    }.toMap)

    val allTachs = tAchievementRepository.getAll.map {
      future =>
        future.filter {
          ach =>
            (!req.fromDate.isDefined || (req.fromDate.isDefined && ach.date >= req.fromDate.get)) &&
              (!req.toDate.isDefined || (req.toDate.isDefined && ach.date <= req.toDate.get))
        }
    }

    val groupedByUser = allTachs.map(d => d.groupBy(_.user).toList)

    val groupedByUserAndTaType = groupedByUser.map {
      future =>
        future.map {
          data => //(userId, Seq[Tachs])
            (data._1, data._2.groupBy(_.taType).toList)
        }
    }

    userIdToUserMap.map {
      map =>
        groupedByUserAndTaType.map {
          future =>
            future.map {
              grouped => //(userId, (taType, Seq[Achs]))
                evalForOneUser(map.get(grouped._1).get, grouped._2)
            }
        }
    }

  }

  def checkIfTAchBelongsToThisUser(tAch: Future[TAchievement], user: User): Future[Boolean] =
    for {
      tAch <- tAch
    } yield (tAch != null && user._id == tAch.user)

  def deleteOne(req: TAchievementDelReq): Future[TAchievementServiceResponse] = {
    val userFromToken = userService.getUserFromToken(req.token)
    val objId = Utils.checkObjectId(req.id)

    val tAchById = tAchievementRepository.getOneById(objId.get)

    OptionT(userFromToken).map { user =>
      checkIfTAchBelongsToThisUser(tAchById, user) map {
        case true =>
          tAchievementRepository.deleteOne(objId.get)
          TAchievementServiceResponse(true, "Deletion Successful")
        case false =>
          TAchievementServiceResponse(false, "Access Denied")
      }
    }.getOrElse {
      Future(TAchievementServiceResponse(false, "Access Denied"))
    }.flatMap(identity)

  }
}
