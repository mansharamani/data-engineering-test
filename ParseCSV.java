import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
	
public class ParseCSV {


    private static final char DEFAULT_SEPARATOR = '\t';
    private static final char DEFAULT_QUOTE = '"';
   public static int lcols=0, mcols=0;
   static String clline="";
   
    public static void main(String[] args) throws Exception {
    	 List<String> cllist = new ArrayList<>();
        String csvFile = "/Users/rohitmansharamani/Downloads/data.tsv";
        int num =1; int num2;
        Scanner scanner = new Scanner(new File(csvFile));
       
        while (scanner.hasNext()) {
        	String myline=scanner.nextLine();
        	
            List<String> line = parseLine(removeEscapeChars(myline));
            nfv(line);
            //System.out.print("rownum "+num);
            if((mcols==line.size())||(num==1) ) {
            for(int i=0;i<line.size();i++) {
            	clline=clline+line.get(i);
            	
            //System.out.print(myline);
            if(i<line.size()-1) {
            	//System.out.print(",");
            	clline=clline+"\t";
            }
            }
            System.out.println(clline);
            cllist.add(clline);
            clline="";
            }
            else {
            }
            num++;
        }
        
        scanner.close();
        writer(cllist);

    }
    private static void nfv( List mLine) {
    	int cols= mLine.size();
    	if (lcols<cols) {
    		lcols=cols;
    	} else {
    		mcols=lcols;
    	}
    	
    	//System.out.println(cols +" "+mcols+" "+lcols);
    	
    }
    
    public static  void writer(List<String> datArrayList) throws IOException {

        PrintWriter pw = null;
        FileOutputStream fo = null;
        File file = null;
        try {
            file = new File("/Users/rohitmansharamani/Downloads/cleandata.tsv");
            pw = new PrintWriter(new FileOutputStream(file));
            fo = new FileOutputStream(file);
            int datList = datArrayList.size();
            for (int i = 0; i < datList; i++) {
                pw.write(datArrayList.get(i).toString() + "\n");
            }
        } finally {
            pw.flush();
            pw.close();
            fo.close();
        }

    }
    private static String removeEscapeChars(String remainingValue) {
        Matcher matcher = Pattern.compile("\\&([^;]{6})", Pattern.CASE_INSENSITIVE).matcher(remainingValue);
        while (matcher.find()) {
            String before = remainingValue.substring(0, matcher.start());
            String after = remainingValue.substring(matcher.start() + 1);
            remainingValue = (before + after);
        }
        return remainingValue;
    }
    public static List<String> parseLine(String cvsLine) {
        return parseLine(cvsLine, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
    }

    public static List<String> parseLine(String cvsLine, char separators) {
        return parseLine(cvsLine, separators, DEFAULT_QUOTE);
    }

    public static List<String> parseLine(String cvsLine, char separators, char customQuote) {

        List<String> result = new ArrayList<>();

        //if empty, return!
        if (cvsLine == null && cvsLine.isEmpty()) {
            return result;
        }

        if (customQuote == ' ') {
            customQuote = DEFAULT_QUOTE;
        }

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuffer curVal = new StringBuffer();
        boolean inQuotes = false;
        boolean startCollectChar = false;
        boolean doubleQuotesInColumn = false;

        char[] chars = cvsLine.toCharArray();

        for (char ch : chars) {

            if (inQuotes) {
                startCollectChar = true;
                if (ch == customQuote) {
                    inQuotes = false;
                    doubleQuotesInColumn = false;
                } else {

                    //Fixed : allow "" in custom quote enclosed
                    if (ch == '\"') {
                        if (!doubleQuotesInColumn) {
                            curVal.append(ch);
                            doubleQuotesInColumn = true;
                        }
                    } else {
                        curVal.append(ch);
                    }

                }
            } else {
                if (ch == customQuote) {

                    inQuotes = true;

                    //Fixed : allow "" in empty quote enclosed
                    if (chars[0] != '"' && customQuote == '\"') {
                        curVal.append('"');
                    }

                    //double quotes in column will hit this!
                    if (startCollectChar) {
                        curVal.append('"');
                    }

                } else if (ch == separators) {

                    result.add(curVal.toString());

                    curVal = new StringBuffer();
                    startCollectChar = false;

                } else if (ch == '\r') {
                    //ignore LF characters
                    continue;
                } else if (ch == '\n') {
                    //the end, break!
                    break;
                } else {
                    curVal.append(ch);
                }
            }

        }

        result.add(curVal.toString());

        return result;
    }

}