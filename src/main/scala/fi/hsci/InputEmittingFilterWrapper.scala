package fi.hsci

import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute
import org.apache.lucene.analysis.{TokenFilter, TokenStream}

final class InputEmittingFilterWrapper(is: TokenStream, fc: TokenStream => TokenFilter) extends TokenFilter(is) {
  private val posIncAttr = addAttribute(classOf[PositionIncrementAttribute])

  private var origPending = false
  private val as = is.cloneAttributes()
  private val f = fc(new TokenFilter(is) {
    final override def incrementToken() = true
  })

  override def incrementToken() = if (origPending) {
    as.copyTo(this)
    posIncAttr.setPositionIncrement(0)
    origPending = false
    true
  } else if (input.incrementToken()) {
      input.copyTo(as)
      if (f.incrementToken() && !f.equals(as))
        origPending = true
      true
  } else false
}
