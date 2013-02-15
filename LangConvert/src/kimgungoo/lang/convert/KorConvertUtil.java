package kimgungoo.lang.convert;

/**
 * English(typos) -> Korean
 * ���̹� �����ο��� ã�� �ҽ�.
 * �ѱۺ�ȯ�� ��Ȯ�� ������ �ȵɰ�� �����״�� ���ϵǵ��� ������.
 * @author kimgungoo
 *
 */
public class KorConvertUtil {
	enum CodeType{chosung, jungsung, jongsung}
	
	public static String toKOR(String eng)
	{		
		if (eng == null)	return eng; 
			
		try {
			return engToKor(eng);
		} catch (Exception e) {
			try {
				return engToKor(turnSpellCase(eng));
			} catch (Exception e2) {
				return eng;
			}			
		}
	}
	
	/**
	 * ��ҹ��� ġȯ
	 * @param eng
	 * @return
	 */
	private static String turnSpellCase(String eng)
	{
		StringBuffer result = new StringBuffer();
		char c;
		int t;
		
		for (int i = 0; i < eng.length(); i++) {
			c = eng.charAt(i);
			
			t = Character.getType(c);
			
			if (t == 1) {
				result.append(String.valueOf(c).toLowerCase());
			} else if (t == 2) {
				result.append(String.valueOf(c).toUpperCase());
			}
		}
		
		return result.toString();
	}
	
	private static String engToKor(String eng) throws Exception
	{
		StringBuffer sb = new StringBuffer();
		int initialCode = 0, medialCode = 0, finalCode = 0;
		int tempMedialCode, tempFinalCode;
		
		for(int i = 0; i < eng.length(); i++)
		{
			 // �ʼ��ڵ� ����
			 initialCode = getCode(CodeType.chosung, eng.substring(i, i+1));
			 
			 if (initialCode == -1) 
			 {
				throw new Exception("cannot make Korean word.");
			 }
			 
			 i++; // �������ڷ�
			 
			 // �߼��ڵ� ����
			 tempMedialCode = getDoubleMedial(i, eng); // �� �ڷ� �̷���� �߼��ڵ� ����
			 
			 if(tempMedialCode != -1){ 
				 medialCode = tempMedialCode;
				 i += 2;
			 }
			 else {// ���ٸ�,
				 medialCode = getSingleMedial(i, eng); // �� �ڷ� �̷���� �߼��ڵ� ����
				 
				 if (medialCode == -1) 
				 {
					throw new Exception("cannot make Korean word.");
				 }
				 
				 i++;
			 }
			 
			 // �����ڵ� ����
			tempFinalCode = getDoubleFinal(i, eng);// �� �ڷ� �̷���� �����ڵ� ����
			if(tempFinalCode != -1)
			{
				finalCode = tempFinalCode;
			
				// �� ������ �߼� ���ڿ� ���� �ڵ带 �����Ѵ�. 
				tempMedialCode = getSingleMedial(i+2, eng);
				if( tempMedialCode != -1 )
				{// �ڵ� ���� ���� ��� 
					finalCode = getSingleFinal(i, eng); // ���� �ڵ� ���� �����Ѵ�.
				}
				else {
					i++;
				}
			}
			else {// �ڵ� ���� ���� ��� ,
				tempMedialCode = getSingleMedial(i+1, eng);// �� ������ �߼� ���ڿ� ���� �ڵ� ����. 
				if(tempMedialCode != -1){// �� ������ �߼� ���ڰ� ������ ���, 
					finalCode = 0;// ���� ���ڴ� ����.
					i--; 
				} 
				else {
					finalCode = getSingleFinal(i, eng); // ���� ���� ����
					if( finalCode == -1 )
						finalCode = 0;
				}
			}
		
			// ������ �ʼ� ���� �ڵ�, �߼� ���� �ڵ�, ���� ���� �ڵ带 ���� �� ��ȯ�Ͽ� ��Ʈ�����ۿ� �ѱ�
			sb.append((char)(0xAC00 + initialCode + medialCode + finalCode));
		}
		
		return sb.toString();
	}
	 
	 /** 
	 * �ش� ���ڿ� ���� �ڵ带 �����Ѵ�. 
	 * @param type �ʼ� : chosung, �߼� : jungsung, ���� : jongsung ���� 
	 * @param char �ش� ���� 
	 */ 
	 private static int getCode(CodeType type, String c)
	 {
		 // �ʼ�
		 String init = "rRseEfaqQtTdwWczxvg";
		 // �߼�
		 String[] mid = {"k","o","i","O","j","p","u","P","h","hk", "ho","hl","y","n","nj","np", "nl", "b", "m", "ml", "l"};
		 // ����
		 String[] fin = {"r", "R", "rt", "s", "sw", "sg", "e", "f", "fr", "fa", "fq", "ft", "fx", "fv", "fg", "a", "q", "qt", "t", "T", "d", "w", "c", "z", "x", "v", "g"};
		
		 switch(type)
		 {
			 case chosung :
				 int index = init.indexOf(c);
				 
				 if( index != -1 )
				 {
					 return index * 21 * 28;
				 }
				 break;
			 case jungsung :		
				 for(int i = 0; i < mid.length; i++)
				 {
					 if(mid[i].equals(c))
					 {
						return i * 28;
					 }
				 }
				 
				 break;
			 case jongsung :
				 for(int i = 0; i < fin.length; i++)
				 {
					 if(fin[i].equals(c))
					 {
						 return i + 1;
					 }
				 }
				 break;
			 default:
				 System.out.println("�߸��� Ÿ�� �Դϴ�");
		 }
		
		return -1;
	 }
	 
	 // �� �ڷ� �� �߼����� �����Ѵ�
	 // �ε����� ����ٸ� -1�� ����
	 private static int getSingleMedial(int i, String eng)
	 {
		 if((i+1) <= eng.length())
		 {
			 return getCode(CodeType.jungsung, eng.substring(i, i+1));
		 }
		 else {
			 return -1;
		 }
	 }
	 
	 // �� �ڷ� �� �߼��� üũ�ϰ�, �ִٸ� ���� �����Ѵ�.
	 // ������ ���ϰ��� -1
	 private static int getDoubleMedial(int i, String eng)
	 {
		 int result;
		 if((i+2) > eng.length())
		 {
			 return -1;
		 }
		 else {
			 result = getCode(CodeType.jungsung, eng.substring(i, i+2));
			 
			 if(result != -1){
				 return result;
			 }else{
				 return -1;
			 }
		 }
	 }
	 
	 // �� �ڷε� �������� �����Ѵ�
	 // �ε����� ����ٸ� -1�� ����
	 private static int getSingleFinal(int i, String eng)
	 {
		 if((i+1) <= eng.length()){
			 return getCode(CodeType.jongsung, eng.substring(i, i+1));
		 }else{
			 return -1;
		 }
	}
	 
	 // �� �ڷε� ������ üũ�ϰ�, �ִٸ� ���� �����Ѵ�.
	 // ������ ���ϰ��� -1
	 private static int getDoubleFinal(int i, String eng)
	 {
		 if((i+2) > eng.length()){
			 return -1;
		 } 
		 else {
			 return getCode(CodeType.jongsung, eng.substring(i, i+2));
		 }
	 }
	 
//	 public static void main(String[] a)
//	 {
//		 System.out.println(TransUtil.toKOR("ww"));
//	 }
}
