package example.shared.lib.util

import example.shared.lib.test.AbstractSpecification

class StringUtilsSpec extends AbstractSpecification {

  "StringUtils" can {

    "toLowerHead" in {
      val ret = StringUtils.toLowerHead("HogeHoge")
      ret must be("hogeHoge")
    }

    "toUpperHead" in {
      val ret = StringUtils.toUpperHead("hogeHoge")
      ret must be("HogeHoge")
    }

    "camelToSnake" in {
      val ret = StringUtils.camelToSnake("hogeHogeHoge")
      ret must be("hoge_hoge_hoge")
    }
  }

}
