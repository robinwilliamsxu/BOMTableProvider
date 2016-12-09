/**
* Copyright 2016 Mentor Graphics Corporation. All Rights Reserved.
* <p>
* Recipients who obtain this code directly from Mentor Graphics use it solely
* for internal purposes to serve as example Java or JavaScript plugins.
* This code may not be used in a commercial distribution. Recipients may
* duplicate the code provided that all notices are fully reproduced with
* and remain in the code. No part of this code may be modified, reproduced,
* translated, used, distributed, disclosed or provided to third parties
* without the prior written consent of Mentor Graphics, except as expressly
* authorized above.
* <p>
* THE CODE IS MADE AVAILABLE "AS IS" WITHOUT WARRANTY OR SUPPORT OF ANY KIND.
* MENTOR GRAPHICS OFFERS NO EXPRESS OR IMPLIED WARRANTIES AND SPECIFICALLY
* DISCLAIMS ANY WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
* OR WARRANTY OF NON-INFRINGEMENT. IN NO EVENT SHALL MENTOR GRAPHICS OR ITS
* LICENSORS BE LIABLE FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL
* DAMAGES (INCLUDING LOST PROFITS OR SAVINGS) WHETHER BASED ON CONTRACT, TORT
* OR ANY OTHER LEGAL THEORY, EVEN IF MENTOR GRAPHICS OR ITS LICENSORS HAVE BEEN
* ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
* <p>
*/

package Comparator;

import java.util.Comparator;

public class AlphaNumComparator<T> implements Comparator<T>
{

	private boolean mCaseInsensitive;

	public AlphaNumComparator()
	{
		mCaseInsensitive = true;
	}

	public AlphaNumComparator(boolean caseInsensitive)
	{
		mCaseInsensitive = caseInsensitive;
	}

	private int compare(char[] a, int ai, char[] b, int bi)
	{
		while (true) {
			// Handle the case where we run of the end of one or both strings.
			if (ai >= a.length && bi >= b.length) {
				return 0;
			}
			if (ai >= a.length) {
				return -1;
			}
			if (bi >= b.length) {
				return +1;
			}

			char ca = a[ai];
			char cb = b[bi];

			if (ca <= '9' && cb <= '9' && ca >= '0' && cb >= '0') {
				// Current character in each string is a digit, compare the contiguous sequence of digits as integers.

				// First sort out how long the digit sequences are.
				int la = 0;
				while (ai < a.length && ca <= '9' && ca >= '0') {
					la += 1;
					if (++ai < a.length) {
						ca = a[ai];
					}
				}

				int lb = 0;
				while (bi < b.length && cb <= '9' && cb >= '0') {
					lb += 1;
					if (++bi < b.length) {
						cb = b[bi];
					}
				}

				int maxlen = Math.max(la, lb);
				int ina = la - maxlen;
				int inb = lb - maxlen;
				int rina = ai - maxlen;
				int rinb = bi - maxlen;

				// Process each digit in turn, starting with the most significant.
				for (int i = 0; i < maxlen; i++) {
					// If one digit sequence is shorter, we pad it with leading zeroes.
					char cha = (ina++ < 0) ? '0' : a[rina];
					char chb = (inb++ < 0) ? '0' : b[rinb];
					rina++;
					rinb++;

					// The most significant digit with a difference defines the result of the comparison.
					if (cha > chb) {
						return +1;
					}
					if (cha < chb) {
						return -1;
					}
				}
				// Indexes already point to the characters following the numbers.
			}
			else {
				// Character from one or both strings is non-digit, compare the characters themselves.
				if (ca > cb) {
					return +1;
				}
				if (ca < cb) {
					return -1;
				}

				ai++;
				bi++;
			}
		}
	}

	@Override public int compare(T a, T b)
	{

		boolean aNull = a == null;
		boolean bNull = b == null;
		if (aNull && bNull) {
			return 0;
		}
		if (aNull) {
			return -1;
		}
		if (bNull) {
			return 1;
		}

		final String aString = a.toString();
		final String bString = b.toString();
		aNull = aString == null;
		bNull = bString == null;

		if (aNull && bNull) {
			return 0;
		}
		if (aNull) {
			return -1;
		}
		if (bNull) {
			return 1;
		}

		int retVal = 0;
		if (mCaseInsensitive) {
			retVal = compare(aString.toUpperCase().toCharArray(), 0, bString.toUpperCase().toCharArray(), 0);
		}
		else {
			retVal = compare(aString.toCharArray(), 0, bString.toCharArray(), 0);
		}
		return retVal;
	}
}