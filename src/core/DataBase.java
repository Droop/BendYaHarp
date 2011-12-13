package core;

import harmonica.Harmonica;
import harmonica.MalformedHarmonicaException;

import java.io.*;
import java.util.regex.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import music.UnexistantNoteException;



import static music.Note.NoteName.*;


public abstract class DataBase<Data> {

	Map<String, Data> datas=
			new HashMap<String, Data>();

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
		Matcher m = getPattern().matcher(chaine);

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
		Set<Data> knownDatas = new HashSet<Data>();
		while (m.find()){
			try{
				Data data = fromMatcher(m);
				datas.put(data.toString(), data);
				if (knownDatas.add(data))
					System.out.println("Added : "+data);
				else
					System.err.println("Already known data for "+m.group(0)+" : "+data);
			}catch (Exception e){
				System.out.println("on file "+f+" : "+m.group(1));
				e.printStackTrace();
			}
		}
	}


	//



	protected abstract Data fromMatcher(Matcher m) throws HarpException;

	protected abstract  Pattern getPattern();

	//


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
		DataBase<Harmonica> hdb = new DataBase<Harmonica>(new File(getDir()+"/db/harmonicas/")){

			@Override
			protected Harmonica fromMatcher(Matcher m) throws MalformedHarmonicaException, UnexistantNoteException {
				return Harmonica.fromMatcher(m);
			}

			@Override
			protected Pattern getPattern() {
				return Harmonica.getPattern();
			}
			
		};

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
