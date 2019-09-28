package amitbansal.controllers

import amitbansal.controllers.requests.{AcademicAddReq, AcademicDelReq, AcademicGetAllReq, AcademicUpdateReq}
import amitbansal.services.AcademicService
import com.google.inject.{Inject, Singleton}
import com.twitter.finatra.http.Controller

@Singleton
class AcademicController @Inject()(academicService: AcademicService) extends Controller {

  prefix("/academic") {
    post("/add") { academicAddReq: AcademicAddReq =>
      academicService.add(academicAddReq)
    }

    delete("/delete") { academicDelReq: AcademicDelReq =>
      academicService.deleteOne(academicDelReq)
    }

    get("/getall") { academicGetAllReq: AcademicGetAllReq =>
      academicService.getAll(academicGetAllReq)
    }

    put("/edit") { academicUpdateReq: AcademicUpdateReq =>
      academicService.edit(academicUpdateReq)
    }
  }
}
