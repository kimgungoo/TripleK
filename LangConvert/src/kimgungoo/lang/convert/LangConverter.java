package kimgungoo.lang.convert;

import java.util.StringTokenizer;

import com.saltlux.in2.util.WordUtil;

public class LangConverter {
	
	private final static int WORD_TYPE_ENG = 2;
	private final static int WORD_TYPE_KOR = 5;
	private final static int WORD_TYPE_MIXED = -1;
	private final static int WORD_TYPE_CHAR = 0;
	
	/**
	 * ������ �ѱ۷�, �ѱ��� �������� ���� ��ü ��ȯ
	 * @param source
	 * @return
	 */
	public String transLang(String source)
	{		
		if (source == null)	return null;		
		
		StringBuffer result = new StringBuffer();		
		int i, type = 0, before = 0, p = 0;
		char ch;
		
		for(i = 0; i < source.length(); i++)
		{
			ch = source.charAt(i);
			type = Character.getType(ch);
			
			if (type == 1) {
				type = 2;
			}
			
			if (i != 0 && type != before) 
			{				
				result.append(transTerm(before, source.substring(p, i)));				
				p = i;
			}
			
			before = type;
		}
		
		result.append(transTerm(type, source.substring(p, i)));
		
		return result.toString();
	}
	
	/**
	 * �ܾ �ƴ϶� ������ ���, 
	 * ȭ�̽�Ʈ���̽��� �����Ͽ� ������ ��ҵ��� �ϳ��� ���� ���Ͻ�Ų��.
	 * @param transType
	 * @param source
	 * @return
	 */
	public String transLang(int transType, String source)
	{
		if (source == null) return null;
		
		if (transType == WORD_TYPE_CHAR || transType == WORD_TYPE_MIXED) {
			return source;
		}
		
		StringBuffer result = new StringBuffer();
		StringTokenizer st = new StringTokenizer(source, " ");
		String term;
		int type;
		
		while (st.hasMoreElements()) {
			term =  st.nextElement().toString();
			type = analysisType(term);
			
			if (transType != type) {
				/*
				 * transType�� �ƴ϶� type�� ù��° �ƱԸ�Ʈ�� �־�� �Ѵ�.
				 * Ÿ���� �ǹ̰� �ƴ϶� ���� Ÿ���� �ǹ�
				 */
				result.append(transTerm(type, term));
			}
			else {
				result.append(term);
			}
			
			result.append(" ");
		}
		
		return result.toString();
	}
	
	/**
	 * ���� ���Ÿ�� üũ
	 * @param str
	 * @return
	 */
	private int analysisType(String str)
	{
		char ch;
		int type;
		byte chk = 0, before = 0;
		for (int i = 0; i < str.length(); i++) 
		{
			ch = str.charAt(i);			
			type = Character.getType(ch);			
			
			if (type == 1 || type == 2) {
				chk = 0;
			}
			else if (type == 5) {
				chk = 1;
			}
			else {
				chk = -1;
			}
			
			if (i != 0 && before != chk) {
				return WORD_TYPE_MIXED;
			}
			
			before = chk;
		}
		
		if (chk == 0) {
			return WORD_TYPE_ENG;
		}
		else if (chk == 1) {
			return WORD_TYPE_KOR;
		}
		else {
			return WORD_TYPE_CHAR;
		}
	}
	
	/**
	 * ��� ��ȯ
	 * @param currentType
	 * @param value
	 * @return
	 */
	protected String transTerm(int currentType, String value)
	{
		if (currentType == WORD_TYPE_KOR) {
			return (WordUtil.hangulToJaso(value));
		}
		else if(currentType == WORD_TYPE_ENG) {
			return (KorConvertUtil.toKOR(value));
		}
		else {
			return value;
		}
	}
	
	public static void main(String[] a)
	{
//		char[] temp = {'��', '��', 'a', 'K'};
//		
//		System.out.println(Character.getType(temp[0]));
//		System.out.println(Character.getType(temp[1]));
//		System.out.println(Character.getType(temp[2]));
//		System.out.println(Character.getType(temp[3]));
		
		LangConverter wa = new LangConverter();
		
//		System.out.println(wa.transAllWord("qer"));
//		
//		System.out.println(TransUtil.toKOR("qer")); 58.181.33.164/users/yslee/saltlux_custom/src/com/saltlux/in2/autodic
		
		System.out.println(wa.transLang("xp!!tm^^xm12 ���� �µ��� tjqTl 1eh dlqslek. dOrlfmf GOtTMQSLEK. bb"));
		
		System.out.println(wa.transLang(LangConverter.WORD_TYPE_ENG, "�� �Ӥ����� ���� �����̤���"));
		System.out.println(wa.transLang(LangConverter.WORD_TYPE_KOR, "QKSWLDML WPDHKD"));
		
		//���ؾ���Ʈ 1ekswl 101gh   ====> ������ �ƴѵ���.
		System.out.println(wa.transLang(LangConverter.WORD_TYPE_KOR, "tjgodkvkxm 1ekswl 101gh"));
	}
}
