package Planit.fakeevent.randomization;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * An implementation of the Randomization class to be used in EventObjectRandomizer.
 * Each implemented method will fill event fields with the appropriate type of input.
 * For event descriptions, only English words and punctutation will be used. For email
 * addresses, phone numbers, and URLs they will be filled in a "well formed" way, but
 * may not exist.
 * @author wginsberg
 *
 */
public class EnglishEventGenerator extends Randomization {

	/*
	 * A file where words are stored, one per line.
	 */
	private static String wordListFilePath = "./src/main/resources/data/word list.txt";
	
	RandomAccessFile wordFile;
	long wordFileLength;
	
	public EnglishEventGenerator() throws IOException{
		wordFile = new RandomAccessFile(new File(wordListFilePath), "r");
		wordFileLength = wordFile.length();
	}
	
	public void close(){
		try {
			wordFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns a random word.
	 * @return
	 */
	public String word(){
		
		/*
		 * Start at a random point in the file and read backwards until a newline is found.
		 * Start reading forwards from that point and end at the next newline. The string
		 * between the newlines is a word.
		 */
		
		boolean foundWord = false;
		long index;
		
		//try until we find the start of a word
		while (!foundWord){
			//start at a random point in the file between index 1 and the end of the file.
			index = 1 + Math.abs(random.nextLong() % (wordFileLength - 1));
			try{
				//go to a random index
				wordFile.seek(index);
				//seek a newline behind
				while (!foundWord){
					//see if we are at a newline
					if ((char) wordFile.readByte() == '\n'){
						foundWord = true;
					}
					//move back one index
					index--;
					wordFile.seek(index);
				}
			}
			catch (IOException ex){
				//hit the beginning of the file
				foundWord = true;
			}
			
			//read two extra bytes to return to the right place
			try {
				wordFile.readByte();
				wordFile.readByte();
			} catch (IOException e1) {
				continue;
			}
			
			//read the word
			StringBuilder sb = new StringBuilder();
			char nextChar = 0;
			while (nextChar != '\n'){
				try {
					nextChar = (char) wordFile.readByte();
					sb.append(nextChar);
				} catch (IOException e) {
					break;
				}
			}
			//there will be a newline at the end, so remove it
			sb.deleteCharAt(sb.length() - 1);
			
			return sb.toString();
		}
		
		//read in the word
		StringBuilder sb = new StringBuilder();
		char nextChar = 0;
		//read in characters until a newline
		while (nextChar != '\n'){
			try{
				nextChar = wordFile.readChar();
				sb.append(nextChar);
			}
			catch (IOException ex){
				break;
			}
		}
		
		return sb.toString();
	}
	
	public String sentence(){
		return sentence(1, 8);
	}
	
	/**
	 * Returns a random sentence of english words.
	 * @return
	 */
	public String sentence(int minWords, int maxWords){
		
		int numWords = integer(minWords, maxWords);
		StringBuilder sb = new StringBuilder();
		
		while (numWords > 0){
			sb.append(word());
			if (numWords > 1){
				//randomly add some punctuation
				if (integer(100) < 5){
					sb.append(character(",:;"));
				}
				sb.append(" ");
			}
			numWords--;
		}
		
		//capitalize the first letter
		sb.setCharAt(0, (char) (sb.charAt(0) - 26));
		//punctuate
		sb.append(". ");
		
		return sb.toString();
	}
	
	@Override
	public String randomTitle() {
		StringBuilder sb = new StringBuilder();
		int length = integer(15);
		for (int i = 0; i < length; i ++){
			sb.append(word());
			if (i < length - 1){
				sb.append(" ");
			}
		}
		
		return sb.toString();
	}

	@Override
	public String randomDescription() {
		int numSentences = integer(3, 10);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < numSentences; i++){
			sb.append(sentence());
		}
		return sb.toString();
	}

	@Override
	public String randomName() {
		return String.format("%s %s", word(), word());
	}

	@Override
	public String randomTwitterHandle() {
		return String.format("@%s%s", word(), word());
	}

	@Override
	public String randomTwitterURL() {
		return String.format("https://twitter.com/%s", word());
	}

	@Override
	public String randomFaceBookURL() {
		return String.format("https://www.facebook.com/%s%s", word(), word());
	}

	@Override
	public String randomURL() {
		return String.format("http://www.%s%s.%s", word(), word(), countryCode());
		
	}

	@Override
	public String randomEmail() {
		return String.format("%s_%s@%s.%s", word(), word(), emailDomain(), countryCode());
	}

	@Override
	public String randomPhoneNumber() {
		return phoneNumber();
	}

	@Override
	public String randomVenueName() {
		return String.format("%s %s %s",word(), word(), word());
	}

	@Override
	public String randomCity() {
		return word();
	}

	@Override
	public String randomStreetNumber() {
		return String.valueOf(integer(1, 16000));
	}

	@Override
	public String randomCountry() {
		return word();
	}

	@Override
	public String randomProvince() {
		return word();
	}

	@Override
	public String randomPostalCode() {
		return String.format("%s%d%s %d%s%d",
				character(UPPER_CASE),
				integer(9),
				character(UPPER_CASE),
				integer(9),
				character(UPPER_CASE),
				integer(9));
	}

	@Override
	public String randomStreetName() {
		return String.format("%s %s", word(), word());
	}

}
