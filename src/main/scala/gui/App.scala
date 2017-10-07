package gui

import common.rich.RichT._
import models.TitleType
import storage.{MovieQuery, MovieStorage}

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scalafx.application.JFXApp.PrimaryStage
import scalafx.application.{JFXApp, Platform}
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.scene.control.{Button, ChoiceBox, Spinner, TextField}
import scalafx.scene.layout.{BorderPane, HBox, VBox}
import scalafx.scene.text.Text
import scalafx.scene.{Node, Scene}

object App extends JFXApp {
  private val movieStorage = MovieStorage
  private val components = mutable.HashMap[String, Node]()
  private def labeledComponent(label: String, node: Node) = {
    components += label -> node
    new HBox(
      new Text(label) {
        wrappingWidth = text.value.length * 7.0
      },
      node) {
      minHeight = 40
    }
  }
  stage = new PrimaryStage {
    maximized = true
    title = "IMDB Querier"
    scene = new Scene {
      root = new BorderPane {borderPane =>
        padding = Insets(25)
        center = new VBox(
          labeledComponent("Title type", new ChoiceBox[String] {
            items = ObservableBuffer[String](TitleType.values().map(_.toPrettyString).toSeq)
            value = "Movie"
          }),
          labeledComponent("Minimum rating", new TextField {
            text = "7.0"
            maxWidth = 50
          }),
          labeledComponent("Minimum voters", new TextField {
            text = "10000"
            maxWidth = 50
          }),
          labeledComponent("From year", {
            val currentYear = java.time.LocalDate.now.getYear
            new Spinner[Int](1900, currentYear, currentYear) {
              editable = true
            }
          }),
          labeledComponent("To year", {
            val currentYear = java.time.LocalDate.now.getYear
            new Spinner[Int](1900, currentYear, currentYear) {
              editable = true
            }
          }),
          new Button("Submit") {
            onMouseClicked = {_ =>
              val mq = MovieQuery(
                minRating = components("Minimum rating").asInstanceOf[TextField].text.value.toDouble,
                minNumVotes = components("Minimum voters").asInstanceOf[TextField].text.value.toInt,
                titleType = components("Title type").asInstanceOf[ChoiceBox[String]].value.value |> TitleType.parse,
                startYear = components("From year").asInstanceOf[Spinner[Int]].value.value,
                endYear = components("From year").asInstanceOf[Spinner[Int]].value.value)
              movieStorage.query(mq).map(_.take(10)).foreach {bms =>
                Platform.runLater {
                  borderPane.bottom = new MovieTable(bms)
                }
              }
            }
          }
        )
      }
    }
  }
}
