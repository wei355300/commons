package com.github.sunnysuperman.commons.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class ContentExtractor {

	private static final int Threshold = 10; // 相邻5个（包括）tag以内的两个sentence被认为是一个段落的paragraph
	private static Map<String, String> cs = new HashMap<String, String>();
	static {
		cs.put("&shy;", "-");
		cs.put("&ndash;", "–");
		cs.put("&mdash;", "—");
		cs.put("&lrm;", "?");
		cs.put("&rlm;", " ");
		cs.put("&zwj;", "?");
		cs.put("&zwnj;", "?");
		cs.put("&emsp;", " ");
		cs.put("&ensp;", " ");
		cs.put("&nbsp;", " ");
		cs.put("&thinsp;", "?");
		cs.put("&quot;", "\"");
		cs.put("&amp;", "&");
		cs.put("&amp;", "&");
		cs.put("&lang;", "?");
		cs.put("&lceil;", "?");
		cs.put("&lfloor;", "?");
		cs.put("&piv;", "?");
		cs.put("&rang;", "?");
		cs.put("&rceil;", "?");
		cs.put("&rfloor;", "?");
		cs.put("&thetasym;", "?");
		cs.put("&upsih;", "?");
		cs.put("&circ;", "?");
		cs.put("&iexcl;", "?");
		cs.put("&brvbar;", "|");
		cs.put("&uml;", "¨");
		cs.put("&macr;", "ˉ");
		cs.put("&acute;", "′");
		cs.put("&cedil;", "?");
		cs.put("&iquest;", "?");
		cs.put("&tilde;", "?");
		cs.put("&lsquo;", "‘");
		cs.put("&rsquo;", "’");
		cs.put("&sbquo;", "?");
		cs.put("&ldquo;", "“");
		cs.put("&rdquo;", "”");
		cs.put("&bdquo;", "?");
		cs.put("&prime;", "′");
		cs.put("&Prime;", "″");
		cs.put("&lsaquo;", "?");
		cs.put("&rsaquo;", "?");
		cs.put("&oline;", "￣");
		cs.put("&oplus;", "⊕");
		cs.put("&minus;", "?");
		cs.put("&otimes;", "?");
		cs.put("&frasl;", "?");
		cs.put("&lowast;", "?");
		cs.put("&lt;", "<");
		cs.put("&gt;", ">");
		cs.put("&plusmn;", "±");
		cs.put("&laquo;", "?");
		cs.put("&raquo;", "?");
		cs.put("&times;", "×");
		cs.put("&divide;", "÷");
		cs.put("&forall;", "?");
		cs.put("&part;", "?");
		cs.put("&exist;", "?");
		cs.put("&empty;", "?");
		cs.put("&nabla;", "?");
		cs.put("&isin;", "∈");
		cs.put("&notin;", "?");
		cs.put("&ni;", "?");
		cs.put("&prod;", "∏");
		cs.put("&sum;", "∑");
		cs.put("&radic;", "√");
		cs.put("&cong;", "∝");
		cs.put("&prop;", "∝");
		cs.put("&ang;", "∠");
		cs.put("&and;", "∧");
		cs.put("&or;", "∨");
		cs.put("&cap;", "∩");
		cs.put("&cup;", "∪");
		cs.put("&int;", "∫");
		cs.put("&there4;", "∴");
		cs.put("&sim;", "～");
		cs.put("&asymp;", "≈");
		cs.put("&ne;", "≠");
		cs.put("&equiv;", "≡");
		cs.put("&le;", "≤");
		cs.put("&ge;", "≥");
		cs.put("&sub;", "?");
		cs.put("&sup;", "?");
		cs.put("&nsub;", "?");
		cs.put("&sube;", "?");
		cs.put("&supe;", "?");
		cs.put("&perp;", "⊥");
		cs.put("&sdot;", "?");
		cs.put("&loz;", "?");
		cs.put("&uarr;", "↑");
		cs.put("&uArr;", "?");
		cs.put("&rarr;", "→");
		cs.put("&lArr;", "?");
		cs.put("&rArr;", "?");
		cs.put("&darr;", "↓");
		cs.put("&dArr;", "?");
		cs.put("&larr;", "←");
		cs.put("&crarr;", "?");
		cs.put("&harr;", "?");
		cs.put("&hArr;", "?");
		cs.put("&cent;", "￠");
		cs.put("&pound;", "￡");
		cs.put("&curren;", "¤");
		cs.put("&yen;", "￥");
		cs.put("&sect;", "§");
		cs.put("&copy;", "?");
		cs.put("&not;", "?");
		cs.put("&reg;", "?");
		cs.put("&deg;", "°");
		cs.put("&micro;", "μ");
		cs.put("&para;", "?");
		cs.put("&middot;", "·");
		cs.put("&dagger;", "?");
		cs.put("&Dagger;", "?");
		cs.put("&bull;", "?");
		cs.put("&hellip;", "…");
		cs.put("&permil;", "‰");
		cs.put("&spades;", "?");
		cs.put("&clubs;", "?");
		cs.put("&hearts;", "?");
		cs.put("&diams;", "?");
		cs.put("&euro;", "€");
		cs.put("&frac14;", "?");
		cs.put("&frac12;", "?");
		cs.put("&frac34;", "?");
		cs.put("&sup1;", "1");
		cs.put("&sup2;", "2");
		cs.put("&sup3;", "3");
		cs.put("&infin;", "∞");
		cs.put("&ordf;", "a");
		cs.put("&aacute;", "á");
		cs.put("&Aacute;", "á");
		cs.put("&Agrave;", "à");
		cs.put("&agrave;", "à");
		cs.put("&acirc;", "a");
		cs.put("&Acirc;", "?");
		cs.put("&Auml;", "?");
		cs.put("&auml;", "?");
		cs.put("&Atilde;", "?");
		cs.put("&atilde;", "?");
		cs.put("&Aring;", "?");
		cs.put("&aring;", "?");
		cs.put("&aelig;", "?");
		cs.put("&AElig;", "?");
		cs.put("&ccedil;", "?");
		cs.put("&Ccedil;", "?");
		cs.put("&eth;", "e");
		cs.put("&ETH;", "D");
		cs.put("&Eacute;", "é");
		cs.put("&eacute;", "é");
		cs.put("&Egrave;", "è");
		cs.put("&egrave;", "è");
		cs.put("&Ecirc;", "ê");
		cs.put("&ecirc;", "ê");
		cs.put("&euml;", "?");
		cs.put("&Euml;", "?");
		cs.put("&fnof;", "?");
		cs.put("&image;", "?");
		cs.put("&Iacute;", "í");
		cs.put("&iacute;", "í");
		cs.put("&igrave;", "ì");
		cs.put("&Igrave;", "ì");
		cs.put("&Icirc;", "?");
		cs.put("&icirc;", "?");
		cs.put("&Iuml;", "?");
		cs.put("&iuml;", "?");
		cs.put("&ntilde;", "?");
		cs.put("&Ntilde;", "?");
		cs.put("&ordm;", "o");
		cs.put("&Oacute;", "ó");
		cs.put("&oacute;", "ó");
		cs.put("&Ograve;", "ò");
		cs.put("&ograve;", "ò");
		cs.put("&Ocirc;", "?");
		cs.put("&ocirc;", "?");
		cs.put("&ouml;", "?");
		cs.put("&Ouml;", "?");
		cs.put("&otilde;", "?");
		cs.put("&Otilde;", "?");
		cs.put("&Oslash;", "?");
		cs.put("&oslash;", "?");
		cs.put("&oelig;", "?");
		cs.put("&OElig;", "?");
		cs.put("&weierp;", "?");
		cs.put("&real;", "?");
		cs.put("&Scaron;", "?");
		cs.put("&scaron;", "?");
		cs.put("&szlig;", "?");
		cs.put("&THORN;", "T");
		cs.put("&thorn;", "t");
		cs.put("&trade;", "?");
		cs.put("&uacute;", "ú");
		cs.put("&Uacute;", "ú");
		cs.put("&Ugrave;", "ù");
		cs.put("&ugrave;", "ù");
		cs.put("&ucirc;", "?");
		cs.put("&Ucirc;", "?");
		cs.put("&Uuml;", "ü");
		cs.put("&uuml;", "ü");
		cs.put("&Yacute;", "Y");
		cs.put("&yacute;", "y");
		cs.put("&Yuml;", "?");
		cs.put("&alpha;", "α");
		cs.put("&Alpha;", "Α");
		cs.put("&beta;", "β");
		cs.put("&Beta;", "Β");
		cs.put("&gamma;", "γ");
		cs.put("&Gamma;", "Γ");
		cs.put("&delta;", "δ");
		cs.put("&Delta;", "Δ");
		cs.put("&Epsilon;", "Ε");
		cs.put("&epsilon;", "ε");
		cs.put("&zeta;", "ζ");
		cs.put("&Zeta;", "Ζ");
		cs.put("&eta;", "η");
		cs.put("&Eta;", "Η");
		cs.put("&theta;", "θ");
		cs.put("&Theta;", "Θ");
		cs.put("&iota;", "ι");
		cs.put("&Iota;", "Ι");
		cs.put("&Kappa;", "Κ");
		cs.put("&kappa;", "κ");
		cs.put("&lambda;", "λ");
		cs.put("&Lambda;", "Λ");
		cs.put("&mu;", "μ");
		cs.put("&Mu;", "Μ");
		cs.put("&nu;", "ν");
		cs.put("&Nu;", "Ν");
		cs.put("&xi;", "ξ");
		cs.put("&Xi;", "Ξ");
		cs.put("&Omicron;", "Ο");
		cs.put("&omicron;", "ο");
		cs.put("&pi;", "π");
		cs.put("&Pi;", "Π");
		cs.put("&rho;", "ρ");
		cs.put("&Rho;", "Ρ");
		cs.put("&Sigma;", "Σ");
		cs.put("&sigma;", "σ");
		cs.put("&sigmaf;", "?");
		cs.put("&Tau;", "Τ");
		cs.put("&tau;", "τ");
		cs.put("&Upsilon;", "Υ");
		cs.put("&upsilon;", "υ");
		cs.put("&Phi;", "Φ");
		cs.put("&phi;", "φ");
		cs.put("&Chi;", "Χ");
		cs.put("&chi;", "χ");
		cs.put("&Psi;", "Ψ");
		cs.put("&psi;", "ψ");
		cs.put("&Omega;", "Ω");
		cs.put("&omega;", "ω");
		cs.put("&alefsym;", "?");
	}

	// private static final float EnglishFactor = 0.59f;
	// public int countWidth(String s){
	// if(s==null)return 0;
	// float length = 0f;
	// for (int i = 0; i < s.length(); i++) {
	// char c = s.charAt(i);
	// if (c > 0x80) {
	// length += 1;
	// } else {
	// length += EnglishFactor;
	// }
	// }
	// return (int) length;
	// }
	// public static String extractByWidth(String detail, int maxLength) {
	// String s=extract(detail,maxLength);
	// if(s==null){
	// return null;
	// }
	// if(s.length()==detail.length()){
	// return s;
	// }
	// return s+"......";
	// }

	public static String extractWithDot(String detail, int maxLength) {
		String s = extract(detail, maxLength);
		if (s == null) {
			return StringUtil.EMPTY;
		}
		if (s.length() == detail.length()) {
			return s;
		}
		return s + "......";
	}

	public static String extract(String detail, int maxLength) {
		if (StringUtil.isEmpty(detail))
			return null;

		String plain = ContentExtractor.extract(detail);

		if (plain == null)
			return null;

		plain = plain.trim();
		if (plain.length() > maxLength) {
			return plain.substring(0, maxLength);
		}

		return plain;
	}

	public static String extract(String detail) {
		if (detail == null)
			return null;
		Stack<Paragraph> paras = new Stack<Paragraph>();
		boolean isInTag = false;
		String content = StringUtil.EMPTY;
		int pos = 0;
		for (int i = 0; i < detail.length(); i++) {
			char c = detail.charAt(i);
			if (c == '<') {
				isInTag = true;
				if (StringUtil.isEmpty(content)) {
					continue;
				}
				Sentence s = new Sentence();
				s.pos = pos;
				s.content = content;
				s.normalizeContent();
				Paragraph p = null;
				if (!paras.empty()) {
					p = paras.peek();
				} else {
					p = new Paragraph();
					paras.push(p);
				}

				int lastPos = p.getLastSentencePos();
				if (lastPos >= 0) {
					if (s.pos - lastPos > Threshold) {
						p = new Paragraph();
						paras.push(p);
					}
				}
				p.addSentence(s);
				content = StringUtil.EMPTY;
			} else if (c == '>') {
				pos++;
				isInTag = false;
			}
			if (!isInTag && c != '>' && c != '\r' && c != '\n' && c != '\t' && c != ' ') {
				content += c;
			}
		}
		Paragraph maxP = null;
		for (Paragraph p : paras) {
			if (maxP == null || maxP.getScore() < p.getScore()) {
				maxP = p;
			}
		}
		if (maxP != null) {
			return maxP.getContent();
		}
		return content;
	}

	public static class Sentence {
		public int pos;
		public String content;
		public Paragraph para;

		public void normalizeContent() {
			if (StringUtil.isEmpty(content)) {
				return;
			}

			int start = content.indexOf('&');
			int end = content.indexOf(';', start);
			boolean hasC = start >= 0 && end > 0 ? true : false;
			while (hasC) {
				String strC = content.substring(start, end + 1);
				String re = cs.containsKey(strC) ? cs.get(strC) : null;
				String tp1 = content.substring(0, start);
				String tp2 = content.substring(end + 1);
				if (re != null) {
					content = tp1 + re + tp2;
				} else {
					content = tp1 + tp2;
				}

				start = content.indexOf('&');
				end = content.indexOf(';', start);
				hasC = start >= 0 && end > 0 ? true : false;
			}
		}
	}

	public static class Paragraph {

		private int FactorLen = 1;

		private int FactorPos = 1;

		public List<Sentence> sentences = new ArrayList<Sentence>();

		public int getScore() {
			int score = 0;
			for (Sentence s : sentences) {
				score += s.content.length() * FactorLen;
			}

			int prevPos = getFirstSentencePos();
			for (Sentence s : sentences) {
				score -= (s.pos - prevPos) * FactorPos;
				prevPos = s.pos;
			}

			return score;
		}

		public String getContent() {
			StringBuffer sb = new StringBuffer();
			for (Sentence s : sentences) {
				sb.append(s.content + " ");
			}

			return sb.toString();
		}

		public void addSentence(Sentence s) {
			sentences.add(s);
		}

		public int getFirstSentencePos() {
			if (sentences.size() == 0) {
				return -1;
			}

			return sentences.get(0).pos;
		}

		public int getLastSentencePos() {
			if (sentences.size() == 0) {
				return -1;
			}

			return sentences.get(sentences.size() - 1).pos;
		}
	}
}
