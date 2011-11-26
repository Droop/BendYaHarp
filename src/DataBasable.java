package src;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface DataBasable {

	public abstract Pattern getPattern();

	public abstract DataBasable fromMatcher(Matcher m) throws Exception;

	public abstract String getName();

}