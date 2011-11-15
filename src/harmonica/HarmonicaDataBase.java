package src.harmonica;

import java.io.*;
import java.util.regex.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static src.harmonica.Note.NoteName.*;


public class HarmonicaDataBase {

	Map<String, Harmonica> harmonicas=
			new HashMap<String, Harmonica>();

	public HarmonicaDataBase(File f){
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
		Pattern harmonica = Pattern.compile(
				"NAME(.*)\n(?:.*\n*)*?BLOW(.*)\n(?:.*\n*)*?DRAW(.*)\n", 
				Pattern.CASE_INSENSITIVE);
		Matcher m = harmonica.matcher(chaine);

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
		Set harp = new HashSet();
		while (m.find()){
			String name = m.group(1);
			String[] blow = m.group(2).replaceFirst("[ \t\n\f\r]++", "").split("[ \t\n\f\r]++");
			String[] draw = m.group(3).replaceFirst("[ \t\n\f\r]++", "").split("[ \t\n\f\r]++");

			try{
				Harmonica parsed = 
						new Harmonica(name, new ReedPlate(blow), new ReedPlate(draw), new Note(DO,3));
				harmonicas.put(name, parsed);	
				if (harp.add(parsed))
					System.out.println("Added : "+harmonicas.get(name));
				else
					System.err.println("Already known harmonica for "+m.group(0)+" : "+parsed);
			}catch (Exception e){
				System.out.println("on file "+f+" : "+m.group(1));
				e.printStackTrace();
			}
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
		HarmonicaDataBase hdb = new HarmonicaDataBase(new File(getDir()+"/db/harmonicas/test"));

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
