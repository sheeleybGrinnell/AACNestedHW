import util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;

/**
 * Creates a set of mappings of an AAC that has two levels,
 * one for categories and then within each category, it has
 * images that have associated text to be spoken. This class
 * provides the methods for interacting with the categories
 * and updating the set of images that would be shown and handling
 * an interactions.
 * 
 * @author Catie Baker & Benjamin Sheeley
 *
 */
public class AACMappings implements AACPage {

	/* FIELDS */
	AssociativeArray<String, AACCategory> mappingArray = new AssociativeArray<String, AACCategory>();

	AACCategory currentCategory = new AACCategory("");

	/**
	 * Creates a set of mappings for the AAC based on the provided
	 * file. The file is read in to create categories and fill each
	 * of the categories with initial items. The file is formatted as
	 * the text location of the category followed by the text name of the
	 * category and then one line per item in the category that starts with
	 * > and then has the file name and text of that image
	 * 
	 * for instance:
	 * img/food/plate.png food
	 * >img/food/icons8-french-fries-96.png french fries
	 * >img/food/icons8-watermelon-96.png watermelon
	 * img/clothing/hanger.png clothing
	 * >img/clothing/collaredshirt.png collared shirt
	 * 
	 * represents the file with two categories, food and clothing
	 * and food has french fries and watermelon and clothing has a
	 * collared shirt
	 * 
	 * @param filename the name of the file that stores the mapping information
	 */
	public AACMappings(String filename) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String currentToken = reader.readLine();
			while (currentToken != null) {
				String[] currentArr = currentToken.split(" ", 2);	
				if (currentToken.contains(">")) {
					currentCategory.addItem(currentArr[0].substring(1), currentArr[1]);
				} else {
					try {
						mappingArray.set(currentArr[0], new AACCategory(currentArr[1]));
						currentCategory = mappingArray.get(currentArr[0]);
					} catch (NullKeyException e) {
						System.err.println("Null Token detected, make it not null");
					} catch (KeyNotFoundException e) {
						System.err.println("key not found AACMapping line 76");
					} catch (ArrayIndexOutOfBoundsException e) {
						System.err.println("Array out of bounds");
					}
				}
				currentToken = reader.readLine();
			}
			currentCategory = new AACCategory("");
			reader.close();
		} catch (IOException e) {
			System.err.println("IOException");
		}
	}
	
	/**
	 * Given the image location selected, it determines the action to be
	 * taken. This can be updating the information that should be displayed
	 * or returning text to be spoken. If the image provided is a category, 
	 * it updates the AAC's current category to be the category associated 
	 * with that image and returns the empty string. If the AAC is currently
	 * in a category and the image provided is in that category, it returns
	 * the text to be spoken.
	 * @param imageLoc the location where the image is stored
	 * @return if there is text to be spoken, it returns that information, otherwise
	 * it returns the empty string
	 * @throws NoSuchElementException if the image provided is not in the current 
	 * category
	 */
	public String select(String imageLoc) {
		try {
			if (!currentCategory.name.matches("")) {
				return this.currentCategory.select(imageLoc);
			}
			AACCategory classToGet = this.mappingArray.get(imageLoc);
			currentCategory = classToGet;
			return "";
		} catch (KeyNotFoundException e) {
			System.err.println("Key not found");
			throw new NoSuchElementException();
		}
	}
	/**
	 * Provides an array of all the images in the current category
	 * @return the array of images in the current category; if there are no images,
	 * it should return an empty array
	 */
	public String[] getImageLocs() {
		if (currentCategory.name.matches("")) {
			String[] resultArr = new String[mappingArray.size()];
			for (int i = 0; i < mappingArray.size(); i++) {
				resultArr[i] = mappingArray.pairs[i].key;
			}
			return resultArr;
		} else {
			return currentCategory.getImageLocs();
		}
	}
	
	/**
	 * Resets the current category of the AAC back to the default
	 * category
	 */
	public void reset() {
		currentCategory = new AACCategory("");
		return;
	}
	
	
	/**
	 * Writes the ACC mappings stored to a file. The file is formatted as
	 * the text location of the category followed by the text name of the
	 * category and then one line per item in the category that starts with
	 * > and then has the file name and text of that image
	 * 
	 * for instance:
	 * img/food/plate.png food
	 * >img/food/icons8-french-fries-96.png french fries
	 * >img/food/icons8-watermelon-96.png watermelon
	 * img/clothing/hanger.png clothing
	 * >img/clothing/collaredshirt.png collared shirt
	 * 
	 * represents the file with two categories, food and clothing
	 * and food has french fries and watermelon and clothing has a 
	 * collared shirt
	 * 
	 * @param filename the name of the file to write the
	 * AAC mapping to
	 */
	public void writeToFile(String filename) {
		try {
			PrintWriter pen = new PrintWriter(filename);
			for (int i = 0; i < mappingArray.size(); i++) {
				String tempKey = mappingArray.pairs[i].key;
				AACCategory tempVal = mappingArray.get(tempKey);
				pen.println(tempKey + " " + tempVal.name);
				for (int j = 0; j < tempVal.pathToWord.size(); j++) {
					String catKey = tempVal.pathToWord.pairs[j].key;
					String catVal = tempVal.pathToWord.get(catKey);
					pen.println(">" + catKey + " " + catVal);
				}
			}
			pen.close();
			return;
		} catch (IOException e) {
			return;
		} catch (KeyNotFoundException e) {
			return;
		}
	}
	
	/**
	 * Adds the mapping to the current category (or the default category if
	 * that is the current category)
	 * @param imageLoc the location of the image
	 * @param text the text associated with the image
	 */
	public void addItem(String imageLoc, String text) {
		try {
			if (currentCategory.name.matches("")) {
			mappingArray.set(imageLoc, new AACCategory(text));
			return;
			} else {
				currentCategory.addItem(imageLoc, text);
				return;
			}
		} catch (NullKeyException e) {
			return;
		}
	}


	/**
	 * Gets the name of the current category
	 * @return returns the current category or the empty string if 
	 * on the default category
	 */
	public String getCategory() {
		return currentCategory.name;
	}


	/**
	 * Determines if the provided image is in the set of images that
	 * can be displayed and false otherwise
	 * @param imageLoc the location of the category
	 * @return true if it is in the set of images that
	 * can be displayed, false otherwise
	 */
	public boolean hasImage(String imageLoc) {
		return mappingArray.hasKey(imageLoc);
	}
}
