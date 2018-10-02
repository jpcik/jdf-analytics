package ch.hevs.jdf

import scala.io.Source

object Users {

  
  def loadUsers={
    val userfile="/users/jpc/switchdrive/projs/data/jdf/users.csv"
    //val atts=smile.read.csv(userfile)
    //smile.data.
    
    val lines=Source.fromFile(userfile).getLines
    lines.next
    var id=0
    val users=lines map {line=>
      val data=line.split(",")
      id+=1;
          //  print(line)

      val state=data(5).split("\\.").last
      User(id,data(0),state,data(1),data(4))
    }
    users
  }
  
  def main(args:Array[String])={
    val fems= loadUsers.filter(u=>u.gender=="female").size
    val males= loadUsers.filter(u=>u.gender=="male").size
    println(s"female: $fems")
    println(s"male: $males")
  }
}

case class User(id:Int,hash:String,state:String,gender:String,lastInteraction:String)