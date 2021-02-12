package fi.hsci

  import org.apache.lucene.analysis.Analyzer.TokenStreamComponents
  import org.apache.lucene.analysis.core.WhitespaceTokenizer
  import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
  import org.apache.lucene.analysis.{Analyzer, LowerCaseFilter, TokenStream, Tokenizer}
  import org.junit.Assert._
  import org.junit.Test

class TestInputEmittingFilterWrapper {
  private def tokenStreamToString(ts: TokenStream): String = {
    val ta = ts.getAttribute(classOf[CharTermAttribute])
    ts.reset()
    val sb = new StringBuilder()
    while (ts.incrementToken()) {
      sb.append(ta.toString)
      sb.append(' ')
    }
    ts.end()
    ts.close()
    if (sb.nonEmpty)
      sb.setLength(sb.length-1)
    sb.toString
  }

  def createAnalyser(tokeniser: String => Tokenizer, filters: ((String, TokenStream) => TokenStream)*): Analyzer = new Analyzer() {
    override def createComponents(fieldName: String) = {
      val t = tokeniser(fieldName)
      new TokenStreamComponents(t,normalize(fieldName,t))
    }

    override def normalize(fieldName: String, src: TokenStream): TokenStream =
      filters.foldLeft(src)((in,f) => f(fieldName,in))
  }

  @Test
  def testInputEmittingFilterWrapper: Unit = {
    val a = createAnalyser(_ => new WhitespaceTokenizer(),(_,ts) => new InputEmittingFilterWrapper(ts,ts => new LowerCaseFilter(ts)))
    assertEquals("test Test abc def DEF",tokenStreamToString(a.tokenStream("","Test abc DEF")))
    assertEquals("test",tokenStreamToString(a.tokenStream("","test")))
  }

}
