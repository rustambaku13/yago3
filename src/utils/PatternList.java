package utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javatools.administrative.Announce;
import javatools.datatypes.Pair;
import basics.Fact;
import basics.FactSource;

/**
 * Replaces patterns by strings
 * 
 * @author Fabian M. Suchanek
 * 
 */
public class PatternList {

	/** Holds the patterns to apply */
	public final List<Pair<Pattern, String>> patterns = new ArrayList<Pair<Pattern, String>>();

	/**
	 * Constructor
	 * 
	 * @throws IOException
	 */
	public PatternList(Theme facts, String relation) throws IOException {
		if (!facts.isAvailableForReading())
			throw new RuntimeException("Theme "+facts+
					" has to be available before using a "+this.getClass().getSimpleName()+
					"! Consider caching it by declaring it in inputCached() of the extracor.");
		load(new FactCollection(facts), relation);
	}

	/**
	 * Constructor
	 * 
	 * @throws IOException
	 */
	public PatternList(FactSource facts, String relation) throws IOException {
		this(new FactCollection(facts), relation);
	}

	/** Constructor */
	public PatternList(FactCollection facts, String relation) {
		load(facts, relation);
	}

	/** Loads all patterns */
	protected void load(FactCollection facts, String relation) {
		Announce.doing("Loading patterns of", relation);
		for (Fact fact : facts.getFactsWithRelation(relation)) {
			patterns.add(new Pair<Pattern, String>(fact.getArgPattern(1), fact
					.getArgJavaString(2)));
		}
		if (patterns.isEmpty()) {
			Announce.warning("No patterns found!");
		}
		Announce.done();
	}

	/** TRUE to print the result after each pattern application*/
	public static boolean printDebug=false;
	
	/** Replaces all patterns in the string */
	public String transform(String input) {
		if (input == null)
			return (null);		
		if(printDebug) System.out.println("Input: "+ input);
		for (Pair<Pattern, String> pattern : patterns) {
			if(printDebug) System.out.println("Pattern: "+ pattern);
			String previous=input;
			input = pattern.first.matcher(input).replaceAll(pattern.second);
			if(printDebug && !previous.equals(input)) System.out.println("--------> "+ input);
			if (input.contains("NIL") && pattern.second.equals("NIL"))
				return (null);
		}
		return (input);
	}
}
