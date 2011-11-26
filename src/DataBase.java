package src;

import java.io.*;
import java.util.regex.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static src.harmonica.Note.NoteName.*;


public class DataBase<Data extends DataBasable> {

	public final Data sample;
	Map<String, DataBasable> datas=
			new HashMap<String, DataBasable>();

	public DataBase(File f){
		if (f.isDirectory()){
			for (File f2 : f.listFiles())
				this.parse(f2);
		} else
			this.parse(f);
	}

	private void parse(File f) {
		System.out.println("parsing "+f);
		String chaine="";
		try{

			//récupération du fichier dans un string
			InputStream ips=new FileInputStream(f); 
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			String ligne;
			while ((ligne=br.readLine())!=null){
				chaine+=ligne+"\n";
			}
			br.close(); 
		}catch (Exception e){
			e.printStackTrace();
		}
		//(?:[^B][^L][^O][^W])(?:[^D][^R][^A][^W])
		//			System.out.println("chaine : "+chaine+"\n###############################");
		//parsage 
		Matcher m = sample.getPattern().matcher(chaine);

		Pattern parenthesis = Pattern.compile("(([^)]*))");

		//			while(m.find())
		//				for (int i = 0; i <= m.groupCount(); i++){
		//					System.out.println("yo : "+i+"\n"+m.group(i));
		//				}

		//			System.out.println(m.group());
		//			System.out.println(m.groupCount());
		//			m.find();
		//			System.out.println(m.group());
		//			System.out.println(m.groupCount());
		//			boolean b = m.matches();
		//			if (b){

		//			}
		Set<DataBasable> knownDatas = new HashSet<DataBasable>();
		while (m.find()){
			try{
				DataBasable data = sample.fromMatcher(m);
				datas.put(data.getName(), data);
				if (knownDatas.add(data))
					System.out.println("Added : "+data);
				else
					System.err.println("Already known data for "+m.group(0)+" : "+data);
			}catch (Exception e){
				System.out.println("on file "+f+" : "+m.group(1));
				e.printStackTrace();
			}
	}


	private static String getDir() {
		if (System.getProperty("user.dir").endsWith("/bin") || System.getProperty("user.dir").endsWith("/src"))
			return
					System.getProperty("user.dir").substring
					(0,	System.getProperty("user.dir").length() - 4)+"/";
		else
			return
					System.getProperty("user.dir")+"/";
	}

	public static void main(String[] args){
		DataBase hdb = new DataBase(new File(getDir()+"/db/harmonicas/test"));

	}

}

//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class LinkGetter {
//	private Pattern htmltag;
//	private Pattern link;
//	private final String root;
//
//	public LinkGetter(String root) {
//		this.root = root;
//		htmltag = Pattern.compile("<a\\b[^>]*href=\"[^>]*>(.*?)</a>");
//		link = Pattern.compile("href=\"[^>]*\">");
//	}
//
//	public List<String> getLinks(String url) {
//		List<String> links = new ArrayList<String>();
//		try {
//			BufferedReader bufferedReader = new BufferedReader(
//					new InputStreamReader(new URL(url).openStream()));
//			String s;
//			StringBuilder builder = new StringBuilder();
//			while ((s = bufferedReader.readLine()) != null) {
//				builder.append(s);
//			}
//
//			Matcher tagmatch = htmltag.matcher(builder.toString());
//			while (tagmatch.find()) {
//				Matcher matcher = link.matcher(tagmatch.group());
//				matcher.find();
//				String link = matcher.group().replaceFirst("href=\"", "")
//						.replaceFirst("\">", "");
//				if (valid(link)) {
//					links.add(makeAbsolute(url, link));
//				}
//			}
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return links;
//	}
//
//	private boolean valid(String s) {
//		if (s.matches("javascript:.*|mailto:.*")) {
//			return false;
//		}
//		return true;
//	}
//
//	private String makeAbsolute(String url, String link) {
//		if (link.matches("http://.*")) {
//			return link;
//		}
//		if (link.matches("/.*") && url.matches(".*$[^/]")) {
//			return url + "/" + link;
//		}
//		if (link.matches("[^/].*") && url.matches(".*[^/]")) {
//			return url + "/" + link;
//		}
//		if (link.matches("/.*") && url.matches(".*[/]")) {
//			return url + link;
//		}
//		if (link.matches("/.*") && url.matches(".*[^/]")) {
//			return url + link;
//		}
//		throw new RuntimeException("Cannot make the link absolute. Url: " + url
//				+ " Link " + link);
//	}
//}
