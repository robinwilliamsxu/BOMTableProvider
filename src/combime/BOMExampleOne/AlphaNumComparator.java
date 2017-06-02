package combime.BOMExampleOne;

// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   AlphaNumComparator.java



import java.util.Comparator;

public class AlphaNumComparator
    implements Comparator
{

    private boolean mCaseInsensitive;
    private boolean mMakeUniqueObjectsUnique;
    private boolean mLeadingZerosSignificant;

    public static AlphaNumComparator getAlphaNumComparatorForTreeUsage()
    {
        return new AlphaNumComparator(true, true);
    }

    public AlphaNumComparator(boolean caseInsensitive, boolean makeUniqueObjectsUnique, boolean leadingZerosSignificant)
    {
        mCaseInsensitive = caseInsensitive;
        mMakeUniqueObjectsUnique = makeUniqueObjectsUnique;
        mLeadingZerosSignificant = leadingZerosSignificant;
    }

    public AlphaNumComparator(boolean caseInsensitive, boolean makeUniqueObjectsUnique)
    {
        this(caseInsensitive, makeUniqueObjectsUnique, false);
    }

    public AlphaNumComparator(boolean caseInsensitive)
    {
        this(caseInsensitive, false);
    }

    public AlphaNumComparator()
    {
        this(true);
    }

    private int compare(char a[], int ai, char b[], int bi)
    {
        do
        {
            if(ai >= a.length && bi >= b.length)
                return 0;
            if(ai >= a.length)
                return -1;
            if(bi >= b.length)
                return 1;
            char ca = a[ai];
            char cb = b[bi];
            if(ca >= '0' && cb >= '0' && ca <= '9' && cb <= '9')
            {
                int la = 0;
                int lb = 0;
                do
                {
                    if(ai >= a.length || ca < '0' || ca > '9')
                        break;
                    la++;
                    if(++ai < a.length)
                        ca = a[ai];
                } while(true);
                do
                {
                    if(bi >= b.length || cb < '0' || cb > '9')
                        break;
                    lb++;
                    if(++bi < b.length)
                        cb = b[bi];
                } while(true);
                int maxlen = Math.max(la, lb);
                int ina = la - maxlen;
                int inb = lb - maxlen;
                int rina = ai - maxlen;
                int rinb = bi - maxlen;
                int i = 0;
                while(i < maxlen) 
                {
                    char cha = ina++ >= 0 ? a[rina] : '0';
                    char chb = inb++ >= 0 ? b[rinb] : '0';
                    rina++;
                    rinb++;
                    if(cha > chb)
                        return 1;
                    if(cha < chb)
                        return -1;
                    i++;
                }
            } else
            {
                if(ca > cb)
                    return 1;
                if(ca < cb)
                    return -1;
                ai++;
                bi++;
            }
        } while(true);
    }

    public int compare(Object a, Object b)
    {
        boolean aNull = a == null || a.toString() == null;
        boolean bNull = b == null || b.toString() == null;
        if(aNull && bNull)
            return 0;
        if(aNull)
            return -1;
        if(bNull)
            return 1;
        String aString = a.toString();
        String bString = b.toString();
        int retVal = 0;
        if(mCaseInsensitive)
            retVal = compare(aString.toUpperCase().toCharArray(), 0, bString.toUpperCase().toCharArray(), 0);
        else
            retVal = compare(aString.toCharArray(), 0, bString.toCharArray(), 0);
        int aLength = aString.length();
        int bLength = bString.length();
        if(mLeadingZerosSignificant && retVal == 0 && aLength != bLength)
            return aLength <= bLength ? -1 : 1;
        if(mMakeUniqueObjectsUnique && retVal == 0 && a != b)
        {
            if(aLength > bLength)
                return 1;
            if(aLength < bLength)
                return -1;
            if(mCaseInsensitive)
                retVal = compare(aString.toCharArray(), 0, bString.toCharArray(), 0);
            if(retVal == 0)
            {
                int aHash = System.identityHashCode(a);
                int bHash = System.identityHashCode(b);
                if(aHash > bHash)
                    return 1;
                if(bHash <= aHash)
                    return retVal;
                else
                    return -1;
            }
        }
        return retVal;
    }

    public static int compare(Object a, Object b, boolean caseInsensitive)
    {
        return (new AlphaNumComparator(caseInsensitive)).compare(a, b);
    }

    public static int compare(Object a, Object b, boolean caseInsensitive, boolean makeUniqueObjectUnique)
    {
        return (new AlphaNumComparator(caseInsensitive, makeUniqueObjectUnique)).compare(a, b);
    }
}
