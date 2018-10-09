package ch.hevs.jdf

import scala.io.Source
import spray.json._
import DefaultJsonProtocol._
import java.util.Date
import scala.util.Try
import java.io.FileWriter
  
case class Cigarette(datetime:String,context:String,alone:String,driving:String,
    mood:String,desire:String,necessary:String,resisting:String)
/*
"cigarettes": [
    {"datetime": "2018-03-01T07:29:51.891140", 
     "context": "CONTEXT_PROF", 
     "alone": "ALONE_YES", 
     "driving": "DRIVING_NO", 
     "mood": "MOOD_NEUTRAL", 
     "desire": "DESIRE_MEDIUM", 
     "necessary": "NECESSARY_YES", 
     "resisting": "RESISTING_TEETH"}, 
     
     */

case class Motivation(start_datetime:String, motivate_pressed:String,
    motivator:String,distraction:String) {
}
/*
 {"helped": [
   {"start_datetime": "2018-03-19T07:46:45.056300", 
    "motivate_pressed": "2018-03-19T07:46:53.771903", 
    "motivator": "cessation.QuoteMotivator", 
    "motivator_content": ["Il faut la quitter comme un esclave qui se libère. Au début, on est un peu perdu.\u00a0C\u2019est petit à petit qu'on apprécie de go\u00fbter à la délivrance.", "], 
    "distraction": "DISTRACT_GAME", 
    "stop_datetime": "2018-03-19T07:47:21.458643"}, 
 * 
 */

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val colorFormat = jsonFormat8(Cigarette)
  //implicit val motivationFormat = jsonFormat4(Motivation)
  implicit object MotivationJsonFormat extends RootJsonFormat[Motivation] {
    val headers = Seq("start_datetime","motivate_pressed","motivator",
        "motivator_content","distraction","stop_datetime")
    def read(value: JsValue) = 
      value.asJsObject.getFields(headers:_*) match {    
        case Seq(JsString(datetime), JsString(pressedDatetime), JsString(motivator),
            JsString(content),JsString(distraction),JsString(stopDatetime)) =>
          Motivation(datetime,pressedDatetime,motivator,distraction)
        case _ => deserializationError("Motivation expected")       
      }
    
    def write(m:Motivation)= ???
  }
}

object Events {
   def loadEvents={
    val eventfile="/home/jpc/data/jdf/data.csv"
    //val userfile="/users/jpc/switchdrive/projs/data/jdf/users.csv"

    
    val lines=Source.fromFile(eventfile).getLines
    lines.next
    var id=0
    val events=lines map {line=>
      val first=line.indexOf(',')
      val second=line.indexOf(',',first+1)
      
      val hash=line.substring(0,first)
      val state=line.substring(first+1,second).split('.').last
      val body=line.substring(second+1)
      //println(hash)
      //println(state)
      Event(hash,state,body)
    }
    events
  }
  
   import MyJsonProtocol._
   
   
   
   
  def main(args:Array[String])={
          
    val timeline=new FileWriter("timeline.csv")
    val motivation=new FileWriter("motivations.csv")
     
    val trackers=loadEvents.foreach(event=>event.stage match {
          
      case "tracker" => 
        val json=event.body.parseJson.asJsObject
        if (json.fields.contains("cigarettes")) {
          val cigaretteList=json.fields("cigarettes").asInstanceOf[JsArray]
          cigaretteList.elements foreach {cig=>
            Try(cig.convertTo[Cigarette]) map {cigarette=>            
              val datetime=cigarette.datetime
              //println(s"${event.hash},cigarette,$datetime")
              timeline.write(s"${event.hash},cigarette,$datetime\n")
            }
          }
        }
        //println(pop.elements.size)
    
      case "cessation"=>
        val json=event.body.parseJson.asJsObject
        val motivateList=json.fields("helped").asInstanceOf[JsArray]
        motivateList.elements foreach {motivate=>
          Try(motivate.convertTo[Motivation]) map{ mot =>
            val hash=event.hash
            println(s"$hash,cessation,${mot.start_datetime}")
            timeline.write(s"$hash,cessation,${mot.start_datetime}\n")
            val motivator=mot.motivator.replace("cessation.", "")
            motivation.write(s"$hash,${mot.start_datetime},${mot.distraction},$motivator\n")
          }
        }
      
      case _ =>
        //println(s"${event.hash},${event.stage}")
    })
    //val males= loadUsers.filter(u=>u.gender=="male").size
    //println(s"cigarettes tracking: $trackers")
    //rintln(s"male: $males")
   motivation.close()
   timeline.close()
   }
   
   
}

case class Event(hash:String,stage:String,body:String) {

  
}

object EventType extends Enumeration{
  val recruitment,tracker,cessation,survey=Value
}
