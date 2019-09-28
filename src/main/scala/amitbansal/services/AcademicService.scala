package amitbansal.services

import amitbansal.controllers.requests.{AcademicAddReq, AcademicDelReq, AcademicGetAllReq, AcademicUpdateReq}
import amitbansal.models.{Academic, User}
import amitbansal.repositories.AcademicRepository
import cats.data.OptionT
import cats.instances.future._
import com.google.inject.{Inject, Singleton}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AcademicService {

  case class AcademicServiceResponse(bool: Boolean, message: String)

}

@Singleton
class AcademicService @Inject()(academicRepository: AcademicRepository, userService: UserService) {

  import AcademicService._

  def add(academicAddReq: AcademicAddReq): Future[AcademicServiceResponse] = {
    val user: Future[Option[User]] = userService.getUserFromToken(academicAddReq.token)

    OptionT(user).map { _ =>
      academicRepository.add(Academic(academicAddReq.rollNo, academicAddReq.name, academicAddReq.batch, academicAddReq.programme, academicAddReq.category))
      AcademicServiceResponse(true, "Record successfully added.")
    }.getOrElse {
      AcademicServiceResponse(false, "Access denied.")
    }
  }

  def edit(academicUpdateReq: AcademicUpdateReq): Future[AcademicServiceResponse] = {

    val objId = Utils.checkObjectId(academicUpdateReq.id)
    val user: Future[Option[User]] = userService.getUserFromToken(academicUpdateReq.token)

    OptionT(user).map { _ =>
      academicRepository.update(objId.get, academicUpdateReq.rollNo, academicUpdateReq.name, academicUpdateReq.batch, academicUpdateReq.programme, academicUpdateReq.category)
      AcademicServiceResponse(true, "Record successfully edited.")
    }.getOrElse {
      AcademicServiceResponse(false, "Access denied.")
    }
  }

  def getAll(academicGetAllReq: AcademicGetAllReq) = {
    academicRepository.getAll().map { ach =>
      for {
        a <- ach
        if ((!academicGetAllReq.programme.isDefined || (academicGetAllReq.programme.isDefined && a.programme == academicGetAllReq.programme.get)) &&
          (!academicGetAllReq.batch.isDefined || (academicGetAllReq.batch.isDefined && a.batch == academicGetAllReq.batch.get)) &&
          (!academicGetAllReq.category.isDefined || (academicGetAllReq.category.isDefined && a.category == academicGetAllReq.category.get)))
      } yield a
    }.map(seq => seq.sortBy(a => a.batch > a.batch))
  }

  def deleteOne(academicDelReq: AcademicDelReq): Future[AcademicServiceResponse] = {
    val objId = Utils.checkObjectId(academicDelReq.id)

    val user: Future[Option[User]] = userService.getUserFromToken(academicDelReq.token)

    OptionT(user).map { _ =>
      academicRepository.delete(objId.get)
      AcademicServiceResponse(true, "Successfully deleted")
    }.getOrElse {
      AcademicServiceResponse(false, "Access denied.")
    }
  }

}
