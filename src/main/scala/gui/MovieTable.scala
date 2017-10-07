package gui

import common.rich.RichT._
import models.BasicMovie

import scalafx.beans.property.{ObjectProperty, StringProperty}
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.TableColumn._
import scalafx.scene.control.{TableColumn, TableView}

class MovieTable(movies: Seq[BasicMovie]) extends TableView[BasicMovie](ObservableBuffer(movies)) {
  columns += new TableColumn[BasicMovie, String] {
    text = "Title"
    cellValueFactory = {_.value.primaryTitle.value |> StringProperty.apply}
    prefWidth = 500
  }
  columns += new TableColumn[BasicMovie, Int] {
    text = "Year"
    cellValueFactory = {mb => ObjectProperty(mb, "Year", mb.value.startYear.get.value)}
    prefWidth = 50
  }
  columns += new TableColumn[BasicMovie, Double] {
    text = "Rating"
    cellValueFactory = {mb => ObjectProperty(mb, "Rating", mb.value.rating.get.averageRating.value)}
    prefWidth = 50
  }
}
