import org.junit.Assert
import org.junit.Test

class Tests {

    @Test
    fun test() {
        Assert.assertEquals(
            "a\nb",
            Utils.unescape("""a\nb""")
        )
    }

    @Test
    fun test2() {
        Assert.assertEquals(
            "a\"b",
            Utils.unescape("""a\"b""")
        )
    }

    @Test
    fun test3() {
        Assert.assertEquals(
            "a\\nb",
            Utils.unescape("""a\\nb""")
        )
    }
}