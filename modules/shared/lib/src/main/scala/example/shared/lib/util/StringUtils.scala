package example.shared.lib.util

object StringUtils {

  def toLowerHead(str: String): String =
    if (str.isEmpty)
      ""
    else
      str.head.toLower + str.tail

  def toUpperHead(str: String): String =
    if (str.isEmpty)
      ""
    else
      str.head.toUpper + str.tail

  def camelToSnake(camel: String): String =
    camel.toCharArray
      .foldLeft(new StringBuilder()) { (sb, char) =>
        if (char.isUpper)
          sb.append('_').append(char.toLower)
        else
          sb.append(char)
      }
      .toString
}
