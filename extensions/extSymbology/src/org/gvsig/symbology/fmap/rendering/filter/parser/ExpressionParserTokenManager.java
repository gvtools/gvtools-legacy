/* Generated By:JavaCC: Do not edit this line. ExpressionParserTokenManager.java */
/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ib��ez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *      +34 963862235
 *   gvsig@gva.es
 *      www.gvsig.gva.es
 *
 *    or
 *
 *   IVER T.I. S.A
 *   Salamanca 50
 *   46005 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */
package org.gvsig.symbology.fmap.rendering.filter.parser;


public class ExpressionParserTokenManager implements ExpressionParserConstants {
	public java.io.PrintStream debugStream = System.out;

	public void setDebugStream(java.io.PrintStream ds) {
		debugStream = ds;
	}

	private final int jjStopStringLiteralDfa_0(int pos, long active0) {
		switch (pos) {
		case 0:
			if ((active0 & 0x10000000L) != 0L) {
				jjmatchedKind = 29;
				return 9;
			}
			if ((active0 & 0x31000L) != 0L) {
				jjmatchedKind = 36;
				return -1;
			}
			if ((active0 & 0x800000L) != 0L)
				return 36;
			return -1;
		case 1:
			if ((active0 & 0x10000000L) != 0L) {
				jjmatchedKind = 29;
				jjmatchedPos = 1;
				return 9;
			}
			if ((active0 & 0x31000L) != 0L) {
				if (jjmatchedPos == 0) {
					jjmatchedKind = 36;
					jjmatchedPos = 0;
				}
				return -1;
			}
			return -1;
		case 2:
			if ((active0 & 0x10000000L) != 0L) {
				jjmatchedKind = 29;
				jjmatchedPos = 2;
				return 9;
			}
			return -1;
		default:
			return -1;
		}
	}

	private final int jjStartNfa_0(int pos, long active0) {
		return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
	}

	private final int jjStopAtPos(int pos, int kind) {
		jjmatchedKind = kind;
		jjmatchedPos = pos;
		return pos + 1;
	}

	private final int jjStartNfaWithStates_0(int pos, int kind, int state) {
		jjmatchedKind = kind;
		jjmatchedPos = pos;
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			return pos + 1;
		}
		return jjMoveNfa_0(state, pos + 1);
	}

	private final int jjMoveStringLiteralDfa0_0() {
		switch (curChar) {
		case 9:
			return jjStopAtPos(0, 3);
		case 32:
			return jjStopAtPos(0, 1);
		case 33:
			jjmatchedKind = 11;
			return jjMoveStringLiteralDfa1_0(0x8000L);
		case 34:
			return jjStartNfaWithStates_0(0, 23, 36);
		case 38:
			return jjMoveStringLiteralDfa1_0(0x20000L);
		case 40:
			return jjStopAtPos(0, 21);
		case 41:
			return jjStopAtPos(0, 22);
		case 42:
			return jjStopAtPos(0, 7);
		case 43:
			return jjStopAtPos(0, 5);
		case 44:
			return jjStopAtPos(0, 24);
		case 45:
			return jjStopAtPos(0, 6);
		case 47:
			return jjStopAtPos(0, 8);
		case 58:
			return jjStopAtPos(0, 25);
		case 59:
			return jjStopAtPos(0, 26);
		case 60:
			jjmatchedKind = 10;
			return jjMoveStringLiteralDfa1_0(0x2000L);
		case 61:
			return jjMoveStringLiteralDfa1_0(0x1000L);
		case 62:
			jjmatchedKind = 9;
			return jjMoveStringLiteralDfa1_0(0x4000L);
		case 91:
			return jjStopAtPos(0, 19);
		case 93:
			return jjStopAtPos(0, 20);
		case 110:
			return jjMoveStringLiteralDfa1_0(0x10000000L);
		case 124:
			return jjMoveStringLiteralDfa1_0(0x10000L);
		default:
			return jjMoveNfa_0(3, 0);
		}
	}

	private final int jjMoveStringLiteralDfa1_0(long active0) {
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(0, active0);
			return 1;
		}
		switch (curChar) {
		case 38:
			if ((active0 & 0x20000L) != 0L)
				return jjStopAtPos(1, 17);
			break;
		case 61:
			if ((active0 & 0x1000L) != 0L)
				return jjStopAtPos(1, 12);
			else if ((active0 & 0x2000L) != 0L)
				return jjStopAtPos(1, 13);
			else if ((active0 & 0x4000L) != 0L)
				return jjStopAtPos(1, 14);
			else if ((active0 & 0x8000L) != 0L)
				return jjStopAtPos(1, 15);
			break;
		case 117:
			return jjMoveStringLiteralDfa2_0(active0, 0x10000000L);
		case 124:
			if ((active0 & 0x10000L) != 0L)
				return jjStopAtPos(1, 16);
			break;
		default:
			break;
		}
		return jjStartNfa_0(0, active0);
	}

	private final int jjMoveStringLiteralDfa2_0(long old0, long active0) {
		if (((active0 &= old0)) == 0L)
			return jjStartNfa_0(0, old0);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(1, active0);
			return 2;
		}
		switch (curChar) {
		case 108:
			return jjMoveStringLiteralDfa3_0(active0, 0x10000000L);
		default:
			break;
		}
		return jjStartNfa_0(1, active0);
	}

	private final int jjMoveStringLiteralDfa3_0(long old0, long active0) {
		if (((active0 &= old0)) == 0L)
			return jjStartNfa_0(1, old0);
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(2, active0);
			return 3;
		}
		switch (curChar) {
		case 108:
			if ((active0 & 0x10000000L) != 0L)
				return jjStartNfaWithStates_0(3, 28, 9);
			break;
		default:
			break;
		}
		return jjStartNfa_0(2, active0);
	}

	private final void jjCheckNAdd(int state) {
		if (jjrounds[state] != jjround) {
			jjstateSet[jjnewStateCnt++] = state;
			jjrounds[state] = jjround;
		}
	}

	private final void jjAddStates(int start, int end) {
		do {
			jjstateSet[jjnewStateCnt++] = jjnextStates[start];
		} while (start++ != end);
	}

	private final void jjCheckNAddTwoStates(int state1, int state2) {
		jjCheckNAdd(state1);
		jjCheckNAdd(state2);
	}

	private final void jjCheckNAddStates(int start, int end) {
		do {
			jjCheckNAdd(jjnextStates[start]);
		} while (start++ != end);
	}

	static final long[] jjbitVec0 = { 0x0L, 0x0L, 0x0L, 0x2000000000000000L };
	static final long[] jjbitVec1 = { 0xfffffffffffffffeL, 0xffffffffffffffffL,
			0xffffffffffffffffL, 0xffffffffffffffffL };
	static final long[] jjbitVec3 = { 0x0L, 0x0L, 0xffffffffffffffffL,
			0xffffffffffffffffL };

	private final int jjMoveNfa_0(int startState, int curPos) {
		int[] nextStates;
		int startsAt = 0;
		jjnewStateCnt = 36;
		int i = 1;
		jjstateSet[0] = startState;
		int j, kind = 0x7fffffff;
		for (;;) {
			if (++jjround == 0x7fffffff)
				ReInitRounds();
			if (curChar < 64) {
				long l = 1L << curChar;
				MatchLoop: do {
					switch (jjstateSet[--i]) {
					case 36:
						if ((0xf7fffffbffffdbffL & l) != 0L)
							jjCheckNAddTwoStates(17, 18);
						else if (curChar == 34) {
							if (kind > 35)
								kind = 35;
						}
						break;
					case 3:
						if ((0xf7fffffbffffdbffL & l) != 0L) {
							if (kind > 36)
								kind = 36;
						} else if (curChar == 34)
							jjCheckNAddTwoStates(17, 18);
						if ((0x3ff000000000000L & l) != 0L) {
							if (kind > 32)
								kind = 32;
							jjCheckNAddStates(0, 7);
						} else if ((0x3000000000L & l) != 0L) {
							if (kind > 29)
								kind = 29;
							jjCheckNAdd(9);
						} else if (curChar == 46)
							jjCheckNAdd(11);
						break;
					case 8:
						if ((0x3000000000L & l) == 0L)
							break;
						if (kind > 29)
							kind = 29;
						jjCheckNAdd(9);
						break;
					case 9:
						if ((0x3ff003000000000L & l) == 0L)
							break;
						if (kind > 29)
							kind = 29;
						jjCheckNAdd(9);
						break;
					case 10:
						if (curChar == 46)
							jjCheckNAdd(11);
						break;
					case 11:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 33)
							kind = 33;
						jjCheckNAddStates(8, 10);
						break;
					case 13:
						if ((0x280000000000L & l) != 0L)
							jjCheckNAdd(14);
						break;
					case 14:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 33)
							kind = 33;
						jjCheckNAddTwoStates(14, 15);
						break;
					case 16:
						if (curChar == 34)
							jjCheckNAddTwoStates(17, 18);
						break;
					case 17:
						if ((0xf7fffffbffffdbffL & l) != 0L)
							jjCheckNAddTwoStates(17, 18);
						break;
					case 18:
						if (curChar == 34 && kind > 35)
							kind = 35;
						break;
					case 19:
						if ((0xf7fffffbffffdbffL & l) != 0L && kind > 36)
							kind = 36;
						break;
					case 20:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 32)
							kind = 32;
						jjCheckNAddStates(0, 7);
						break;
					case 21:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 32)
							kind = 32;
						jjCheckNAdd(21);
						break;
					case 22:
						if ((0x3ff000000000000L & l) != 0L)
							jjCheckNAddTwoStates(22, 23);
						break;
					case 23:
						if (curChar != 46)
							break;
						if (kind > 33)
							kind = 33;
						jjCheckNAddStates(11, 13);
						break;
					case 24:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 33)
							kind = 33;
						jjCheckNAddStates(11, 13);
						break;
					case 26:
						if ((0x280000000000L & l) != 0L)
							jjCheckNAdd(27);
						break;
					case 27:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 33)
							kind = 33;
						jjCheckNAddTwoStates(27, 15);
						break;
					case 28:
						if ((0x3ff000000000000L & l) != 0L)
							jjCheckNAddTwoStates(28, 29);
						break;
					case 30:
						if ((0x280000000000L & l) != 0L)
							jjCheckNAdd(31);
						break;
					case 31:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 33)
							kind = 33;
						jjCheckNAddTwoStates(31, 15);
						break;
					case 32:
						if ((0x3ff000000000000L & l) != 0L)
							jjCheckNAddStates(14, 16);
						break;
					case 34:
						if ((0x280000000000L & l) != 0L)
							jjCheckNAdd(35);
						break;
					case 35:
						if ((0x3ff000000000000L & l) != 0L)
							jjCheckNAddTwoStates(35, 15);
						break;
					default:
						break;
					}
				} while (i != startsAt);
			} else if (curChar < 128) {
				long l = 1L << (curChar & 077);
				MatchLoop: do {
					switch (jjstateSet[--i]) {
					case 36:
					case 17:
						jjCheckNAddTwoStates(17, 18);
						break;
					case 3:
						if (kind > 36)
							kind = 36;
						if ((0x7fffffe87fffffeL & l) != 0L) {
							if (kind > 29)
								kind = 29;
							jjCheckNAdd(9);
						}
						if (curChar == 102)
							jjstateSet[jjnewStateCnt++] = 6;
						else if (curChar == 116)
							jjstateSet[jjnewStateCnt++] = 2;
						break;
					case 0:
						if (curChar == 101 && kind > 27)
							kind = 27;
						break;
					case 1:
						if (curChar == 117)
							jjCheckNAdd(0);
						break;
					case 2:
						if (curChar == 114)
							jjstateSet[jjnewStateCnt++] = 1;
						break;
					case 4:
						if (curChar == 115)
							jjCheckNAdd(0);
						break;
					case 5:
						if (curChar == 108)
							jjstateSet[jjnewStateCnt++] = 4;
						break;
					case 6:
						if (curChar == 97)
							jjstateSet[jjnewStateCnt++] = 5;
						break;
					case 7:
						if (curChar == 102)
							jjstateSet[jjnewStateCnt++] = 6;
						break;
					case 8:
					case 9:
						if ((0x7fffffe87fffffeL & l) == 0L)
							break;
						if (kind > 29)
							kind = 29;
						jjCheckNAdd(9);
						break;
					case 12:
						if ((0x2000000020L & l) != 0L)
							jjAddStates(17, 18);
						break;
					case 15:
						if ((0x5000000050L & l) != 0L && kind > 33)
							kind = 33;
						break;
					case 19:
						if (kind > 36)
							kind = 36;
						break;
					case 25:
						if ((0x2000000020L & l) != 0L)
							jjAddStates(19, 20);
						break;
					case 29:
						if ((0x2000000020L & l) != 0L)
							jjAddStates(21, 22);
						break;
					case 33:
						if ((0x2000000020L & l) != 0L)
							jjAddStates(23, 24);
						break;
					default:
						break;
					}
				} while (i != startsAt);
			} else {
				int hiByte = (int) (curChar >> 8);
				int i1 = hiByte >> 6;
				long l1 = 1L << (hiByte & 077);
				int i2 = (curChar & 0xff) >> 6;
				long l2 = 1L << (curChar & 077);
				MatchLoop: do {
					switch (jjstateSet[--i]) {
					case 36:
					case 17:
						if (jjCanMove_1(hiByte, i1, i2, l1, l2))
							jjCheckNAddTwoStates(17, 18);
						break;
					case 3:
						if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
							if (kind > 29)
								kind = 29;
							jjCheckNAdd(9);
						}
						if (jjCanMove_1(hiByte, i1, i2, l1, l2)) {
							if (kind > 36)
								kind = 36;
						}
						break;
					case 8:
					case 9:
						if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
							break;
						if (kind > 29)
							kind = 29;
						jjCheckNAdd(9);
						break;
					case 19:
						if (jjCanMove_1(hiByte, i1, i2, l1, l2) && kind > 36)
							kind = 36;
						break;
					default:
						break;
					}
				} while (i != startsAt);
			}
			if (kind != 0x7fffffff) {
				jjmatchedKind = kind;
				jjmatchedPos = curPos;
				kind = 0x7fffffff;
			}
			++curPos;
			if ((i = jjnewStateCnt) == (startsAt = 36 - (jjnewStateCnt = startsAt)))
				return curPos;
			try {
				curChar = input_stream.readChar();
			} catch (java.io.IOException e) {
				return curPos;
			}
		}
	}

	static final int[] jjnextStates = { 21, 22, 23, 28, 29, 32, 33, 15, 11, 12,
			15, 24, 25, 15, 32, 33, 15, 13, 14, 26, 27, 30, 31, 34, 35, };

	private static final boolean jjCanMove_0(int hiByte, int i1, int i2,
			long l1, long l2) {
		switch (hiByte) {
		case 255:
			return ((jjbitVec0[i2] & l2) != 0L);
		default:
			return false;
		}
	}

	private static final boolean jjCanMove_1(int hiByte, int i1, int i2,
			long l1, long l2) {
		switch (hiByte) {
		case 0:
			return ((jjbitVec3[i2] & l2) != 0L);
		default:
			if ((jjbitVec1[i1] & l1) != 0L)
				return true;
			return false;
		}
	}

	public static final String[] jjstrLiteralImages = { "", null, null, null,
			null, "\53", "\55", "\52", "\57", "\76", "\74", "\41", "\75\75",
			"\74\75", "\76\75", "\41\75", "\174\174", "\46\46", null, "\133",
			"\135", "\50", "\51", "\42", "\54", "\72", "\73", null,
			"\156\165\154\154", null, null, null, null, null, null, null, null, };
	public static final String[] lexStateNames = { "DEFAULT", };
	static final long[] jjtoToken = { 0x1b3ffbffe1L, };
	static final long[] jjtoSkip = { 0x1eL, };
	protected SimpleCharStream input_stream;
	private final int[] jjrounds = new int[36];
	private final int[] jjstateSet = new int[72];
	protected char curChar;

	public ExpressionParserTokenManager(SimpleCharStream stream) {
		if (SimpleCharStream.staticFlag)
			throw new Error(
					"ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
		input_stream = stream;
	}

	public ExpressionParserTokenManager(SimpleCharStream stream, int lexState) {
		this(stream);
		SwitchTo(lexState);
	}

	public void ReInit(SimpleCharStream stream) {
		jjmatchedPos = jjnewStateCnt = 0;
		curLexState = defaultLexState;
		input_stream = stream;
		ReInitRounds();
	}

	private final void ReInitRounds() {
		int i;
		jjround = 0x80000001;
		for (i = 36; i-- > 0;)
			jjrounds[i] = 0x80000000;
	}

	public void ReInit(SimpleCharStream stream, int lexState) {
		ReInit(stream);
		SwitchTo(lexState);
	}

	public void SwitchTo(int lexState) {
		if (lexState >= 1 || lexState < 0)
			throw new TokenMgrError("Error: Ignoring invalid lexical state : "
					+ lexState + ". State unchanged.",
					TokenMgrError.INVALID_LEXICAL_STATE);
		else
			curLexState = lexState;
	}

	protected Token jjFillToken() {
		Token t = Token.newToken(jjmatchedKind);
		t.kind = jjmatchedKind;
		String im = jjstrLiteralImages[jjmatchedKind];
		t.image = (im == null) ? input_stream.GetImage() : im;
		t.beginLine = input_stream.getBeginLine();
		t.beginColumn = input_stream.getBeginColumn();
		t.endLine = input_stream.getEndLine();
		t.endColumn = input_stream.getEndColumn();
		return t;
	}

	int curLexState = 0;
	int defaultLexState = 0;
	int jjnewStateCnt;
	int jjround;
	int jjmatchedPos;
	int jjmatchedKind;

	public Token getNextToken() {
		int kind;
		Token specialToken = null;
		Token matchedToken;
		int curPos = 0;

		EOFLoop: for (;;) {
			try {
				curChar = input_stream.BeginToken();
			} catch (java.io.IOException e) {
				jjmatchedKind = 0;
				matchedToken = jjFillToken();
				return matchedToken;
			}

			try {
				input_stream.backup(0);
				while (curChar <= 13 && (0x2400L & (1L << curChar)) != 0L)
					curChar = input_stream.BeginToken();
			} catch (java.io.IOException e1) {
				continue EOFLoop;
			}
			jjmatchedKind = 0x7fffffff;
			jjmatchedPos = 0;
			curPos = jjMoveStringLiteralDfa0_0();
			if (jjmatchedKind != 0x7fffffff) {
				if (jjmatchedPos + 1 < curPos)
					input_stream.backup(curPos - jjmatchedPos - 1);
				if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L) {
					matchedToken = jjFillToken();
					return matchedToken;
				} else {
					continue EOFLoop;
				}
			}
			int error_line = input_stream.getEndLine();
			int error_column = input_stream.getEndColumn();
			String error_after = null;
			boolean EOFSeen = false;
			try {
				input_stream.readChar();
				input_stream.backup(1);
			} catch (java.io.IOException e1) {
				EOFSeen = true;
				error_after = curPos <= 1 ? "" : input_stream.GetImage();
				if (curChar == '\n' || curChar == '\r') {
					error_line++;
					error_column = 0;
				} else
					error_column++;
			}
			if (!EOFSeen) {
				input_stream.backup(1);
				error_after = curPos <= 1 ? "" : input_stream.GetImage();
			}
			throw new TokenMgrError(EOFSeen, curLexState, error_line,
					error_column, error_after, curChar,
					TokenMgrError.LEXICAL_ERROR);
		}
	}

}
