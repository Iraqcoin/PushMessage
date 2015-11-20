package com.vng.zing.pusheventmessage.common;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;

/**
 *
 * @author tamnd2
 */
public class UtilHelper {

	public static final Charset charset = Charset.forName("UTF-8");
	public static final CharsetEncoder encoder = charset.newEncoder();
	public static final CharsetDecoder decoder = charset.newDecoder();
	private static final Random rand = new Random();

	public static String md5(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte byteData[] = md.digest();
			//convert the byte to hex format method 1
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
			return sb.toString();
		} catch (Exception ex) {
			return "";
		}
	}

	public static ByteBuffer strToBB(String msg) {
		try {
			return encoder.encode(CharBuffer.wrap(msg));
		} catch (Exception e) {
		}
		return null;
	}

	public static ByteBuffer longToBB(long l) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(8);
		buffer.putLong(l);
		buffer.flip();
		return buffer;
	}

	public static long bbToL(ByteBuffer buffer) {
		int old_position = buffer.position();
		long l = buffer.getLong();
		buffer.position(old_position);
		return l;
	}

	public static String bbToStr(ByteBuffer buffer) {
		String data = "";
		try {
			int old_position = buffer.position();
			data = decoder.decode(buffer).toString();
			buffer.position(old_position);
		} catch (Exception e) {
		}
		return data;
	}

	public static String toASCII(String text) {
		if (text == null) {
			return "";
		}
		text = text.replaceAll(" ", "");
		text = text.toLowerCase();
		text = text.replaceAll("à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ", "a");
		text = text.replaceAll("è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ", "e");
		text = text.replaceAll("ì|í|ị|ỉ|ĩ", "i");
		text = text.replaceAll("ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ", "o");
		text = text.replaceAll("ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ", "u");
		text = text.replaceAll("ỳ|ý|ỵ|ỷ|ỹ", "y");
		text = text.replaceAll("đ", "d");
		text = text.replaceAll("!|@|%|\\^|\\*|\\(|\\)|\\+|\\=|\\<|\\>|\\?|\\/|,|\\.|\\:|\\;|\\'|\\\"|\\&|\\#|\\[|\\]|~|$|_", "-");
		text = text.replaceAll("-+-", "-");
		text = text.replaceAll("^\\-+|\\-+$", "");
		return text;
	}

	public static String filterUnicodeToASCII(String str) {
		String rs = "";
		String[] marTViet = new String[]{"à", "á", "ạ", "ả", "ã", "â", "ầ", "ấ", "ậ", "ẩ", "ẫ", "ă", "ằ", "ắ", "ặ", "ẳ", "ẵ", "è", "é", "ẹ", "ẻ", "ẽ", "ê", "ề", "ế", "ệ", "ể", "ễ", "ì", "í", "ị", "ỉ", "ĩ", "ò", "ó", "ọ", "ỏ", "õ", "ô", "ồ", "ố", "ộ", "ổ", "ỗ", "ơ", "ờ", "ớ", "ợ", "ở", "ỡ", "ù", "ú", "ụ", "ủ", "ũ", "ư", "ừ", "ứ", "ự", "ử", "ữ", "ỳ", "ý", "ỵ", "ỷ", "ỹ", "đ", "À", "Á", "Ạ", "Ả", "Ã", "Â", "Ầ", "Ấ", "Ậ", "Ẩ", "Ẫ", "Ă", "Ằ", "Ắ", "Ặ", "Ẳ", "Ẵ", "È", "É", "Ẹ", "Ẻ", "Ẽ", "Ê", "Ề", "Ế", "Ệ", "Ể", "Ễ", "Ì", "Í", "Ị", "Ỉ", "Ĩ", "Ò", "Ó", "Ọ", "Ỏ", "Õ", "Ô", "Ồ", "Ố", "Ộ", "Ổ", "Ỗ", "Ơ", "Ờ", "Ớ", "Ợ", "Ở", "Ỡ", "Ù", "Ú", "Ụ", "Ủ", "Ũ", "Ư", "Ừ", "Ứ", "Ự", "Ử", "Ữ", "Ỳ", "Ý", "Ỵ", "Ỷ", "Ỹ", "Đ"};
		String[] marKoDau = new String[]{"a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "i", "i", "i", "i", "i", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "u", "u", "u", "u", "u", "u", "u", "u", "u", "u", "u", "y", "y", "y", "y", "y", "d", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "E", "E", "E", "E", "E", "E", "E", "E", "E", "E", "E", "I", "I", "I", "I", "I", "O", "O", "O", "O", "O", "O", "O", "O", "O", "O", "O", "O", "O", "O", "O", "O", "O", "U", "U", "U", "U", "U", "U", "U", "U", "U", "U", "U", "Y", "Y", "Y", "Y", "Y", "D"};
		rs = str;
		for (int index = 0; index < marTViet.length; ++index) {
			rs = rs.replace(marTViet[index], marKoDau[index]);

		}
		return rs;
	}

	public static long getTime(String strDate, String format) {
		if (format == null) {
			format = "dd/MM/yyyy HH:mm:ss";
		}
		long result = 0;
		try {
			if (strDate != null && !strDate.isEmpty()) {
				DateFormat formatter;
				Date date;
				formatter = new SimpleDateFormat(format);
				date = (Date) formatter.parse(strDate);
				result = date.getTime();
			} else {
				result = System.currentTimeMillis();
			}
		} catch (ParseException e) {
			System.out.println("Exception :" + e);
		}
		return result;
	}

	public static String getStrDate(long time, String format) {
		if (format == null || format.isEmpty()) {
			format = "dd - MM - yyyy";
		}
		DateFormat dateFormat = new SimpleDateFormat(format);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		String result = dateFormat.format(cal.getTime());
		return (result != null ? result : "");
	}

	public static boolean validateURL(String url) {
//		String rex = "((([A-Za-z]{3,9}:(?:\\/\\/)?)(?:[-;:&=\\+\\$,\\w]+@)?[A-Za-z0-9.-]+|(?:www.|[-;:&=\\+\\$,\\w]+@)[A-Za-z0-9.-]+)((?:\\/[\\+~%\\/.\\w-_]*)?\\??(?:[-\\+=&;%@.\\w_]*)#?(?:[\\w]*))?)";
		String rex = "^(https?:\\/\\/)" + // protocol
				"((([a-z\\d]([a-z\\d-]*[a-z\\d])*)\\.)+[a-z]{2,}|" + // domain name
				"((\\d{1,3}\\.){3}\\d{1,3}))" + // OR ip (v4) address
				"(\\:\\d+)?(\\/[-a-z\\d%_.~+]*)*" + // port and path
				"(\\?[;&a-z\\d%_.~+=-]*)?" + // query string
				"(\\#[-a-z\\d_]*)?$"; // fragment locater

//		return url.matches(rex);
		return true;
	}

	public static boolean validateEmail(String hex) {
		String EMAIL_PATTERN
				= "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(hex);
		return matcher.matches();
	}

	public static boolean validateUsername(String hex) {
		String USERNAME_PATTERN = "^(?=.{6,24}$)(?![_.])(?![0-9])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$";
		Pattern pattern = Pattern.compile(USERNAME_PATTERN);
		Matcher matcher = pattern.matcher(hex);
		return matcher.matches();
	}

	public static boolean validateDisplayname(String hex) {
		hex = filterUnicodeToASCII(hex);
		String DISPLAYNAME_PATTERN = "^[a-zA-Z0-9 ]{1,50}$";
		Pattern pattern = Pattern.compile(DISPLAYNAME_PATTERN);
		Matcher matcher = pattern.matcher(hex);
		return matcher.matches();
	}

	public static boolean validatePhone(String phone) {
		String PHONE_PATTERN = "^\\+?[0-9]{6,24}$";
		Pattern pattern = Pattern.compile(PHONE_PATTERN);
		Matcher matcher = pattern.matcher(phone);
		return matcher.matches();
	}

	public static List<String> splitStrToList(String str, String regex) {
		String[] tempArr = str.split(regex);
		List<String> result = new ArrayList();
		if (tempArr != null && tempArr.length > 0) {
			Collections.addAll(result, tempArr);
		}
		return result;
	}

	public static String zenCaptcha(int numchar) {
		String code = "";
		for (int i = 0; i < numchar; ++i) {
			code += String.valueOf(rand.nextInt(9));
		}
		return code;
	}

	public static boolean isAjaxRequest(HttpServletRequest request) {
		boolean isAjax = false;
		if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
			isAjax = true;
		}
		return isAjax;
	}

	public static ByteBuffer getByteBufferFromFile(String path) {
		ByteBuffer buff = null;

		File fcover = new File(path);
		if (fcover.exists()) {
			FileInputStream fileStream;
			try {
				fileStream = new FileInputStream(fcover);
				ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
				byte[] read = new byte[2048];
				int i = 0;
				while ((i = fileStream.read(read)) > 0) {
					byteArray.write(read, 0, i);
				}
				fileStream.close();
				buff = ByteBuffer.wrap(byteArray.toByteArray());
			} catch (Exception ex) {
			}
		}
		return buff;
	}

	public static String strtrim(String str) {
		String rs = str;
		int length;
		do {
			length = rs.length();
			rs = rs.replace("  ", " ");
		} while (length != rs.length());
		return rs;
	}

	public static String hashMediaName(String filename) {
		String rs = "";
		int pos = filename.lastIndexOf(".");
		if (pos > -1) {
			String name = filename.substring(0, pos);
			String type = filename.substring(pos + 1, filename.length());
			name = strtrim(name);
			name = filterUnicodeToASCII(name);
			name = md5(name);
			name += "_" + System.currentTimeMillis();
			rs = name + "." + type;
		}
		return rs;
	}

	public static String escapeHTML(String html) {
		if (html != null) {
			return html.replaceAll("\\<.*?\\>", "");
		}
		return "";
	}

	public static String xssRemove(String content) {
		if (content == null) {
			return "";
		}
		return content.replaceAll("(?i)<script.*?>.*?</script.*?>", "") // case 1
				.replaceAll("(?i)<.*?javascript:.*?>.*?</.*?>", "") // case 2
				.replaceAll("(?i)<.*?\\s+on.*?>.*?</.*?>", "");     // case 3
	}

	public static String safeStr(String content) {
		content = xssRemove(content);
		content = escapeHTML(content);
		content = strtrim(content);
		return content;
	}

	public static TBase deserialize(ByteBuffer buffer, TBase tBase) {
		try {
			TDeserializer deserializer = new TDeserializer();
			deserializer.deserialize(tBase, buffer);
			return tBase;
		} catch (TException ex) {
		}
		return null;
	}

	public static ByteBuffer serialize(TBase tBase) {
		try {
			TSerializer serializer = new TSerializer();
			ByteBuffer ret = ByteBuffer.wrap(serializer.serialize(tBase));
			return ret;
		} catch (TException ex) {
		}
		return null;
	}

    public static boolean isTextFile(FileItem file) {
        try {
            String fileType = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            if (file != null && file.getSize() > 0
                    && (fileType.equalsIgnoreCase("csv")
                    || fileType.equalsIgnoreCase("txt")
                    || fileType.equalsIgnoreCase("log"))) {
                InputStream fileInputStream = null;

                fileInputStream = file.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String strPhone = "";
                int testTimes = 10;
                int[] rowNum = new int[testTimes];
                int cnt = 0;
                while ((strPhone = bufferedReader.readLine()) != null) {
                    if (null != strPhone) {
                        int curColNum = strPhone.split(",").length;
                        rowNum[cnt] = curColNum;
                        cnt++;
                        if (cnt >= testTimes) {
                            break;
}
                    }
                }
                cnt = 0;
                for (int i = 1; i < rowNum.length; i++) {
                    if (i > 2 && rowNum[i] == rowNum[i - 1]) {
                        cnt++;
                    }
                }
                return cnt == 0 || (cnt > 5);

            } else {
                return false;
            }
        } catch (IOException ex) {
            return false;
        }
    }

}
