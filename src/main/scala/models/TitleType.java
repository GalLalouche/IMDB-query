package models;

import common.rich.primitives.RichString$;

public enum TitleType {
  MOVIE,
  SHORT,
  TVEPISODE,
  TVMINISERIES,
  TVMOVIE,
  TVSERIES,
  TVSHORT,
  TVSPECIAL,
  VIDEO,
  VIDEOGAME;

  private static String capitalize(String s) {
    return RichString$.MODULE$.richString(s).capitalize();
  }

  public String toPrettyString() {
    if (this == VIDEOGAME)
      return "Video Game";
    String s = toString();
    if (s.startsWith("TV"))
      return "TV " + capitalize(s.substring(3));
    return capitalize(s);
  }

  public static TitleType parse(String s) {
    return valueOf(s.toUpperCase().replaceAll(" ", ""));
  }
}
