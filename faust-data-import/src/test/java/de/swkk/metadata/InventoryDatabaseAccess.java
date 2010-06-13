package de.swkk.metadata;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.faustedition.Log;


public class InventoryDatabaseAccess {

	private InventoryDatabase faustInventory;
	private ArchiveDatabase archiveDatabase;

	@Before
	public void setUp() throws Exception {
		faustInventory = InventoryDatabase.parse();
		archiveDatabase = ArchiveDatabase.parse();
	}

	@Test
	public void callNumbersGiven() {
		for (AllegroRecord record : faustInventory) {
			if (faustInventory.getCallNumber(record) == null) {
				faustInventory.dump(record, System.out);
				Assert.fail(String.format("Record %d without call number"));
			}

		}
	}

	@Test
	public void callNumbersWellformed() {
		Set<Integer> portfolioSet = new TreeSet<Integer>();
		Set<String> subPortfolioSet = new TreeSet<String>();
		Set<Integer> fileSet = new TreeSet<Integer>();
		Set<Integer> subFileSet = new TreeSet<Integer>();
		Set<String> subFilePrimarySuffixSet = new TreeSet<String>();
		Set<String> subFileSecondarySuffixSet = new TreeSet<String>();
		Set<String> contentSet = new TreeSet<String>();

		for (AllegroRecord record : faustInventory) {
			GSACallNumber callNumber = faustInventory.getCallNumber(record);
			portfolioSet.add(callNumber.getPortfolio() == null ? 25 : callNumber.getPortfolio());
			subPortfolioSet.add(callNumber.getSubPortfolio());
			fileSet.add(callNumber.getFile());
			if (callNumber.getSubFile() != null) {
				subFileSet.add(callNumber.getSubFile());
			}
			if (callNumber.getSubFilePrimarySuffix() != null) {
				subFilePrimarySuffixSet.add(callNumber.getSubFilePrimarySuffix());
			}
			if (callNumber.getSubFileSecondarySuffix() != null) {
				subFileSecondarySuffixSet.add(callNumber.getSubFileSecondarySuffix());
			}
			if (callNumber.getContentSpec() != null) {
				contentSet.add(callNumber.getContentSpec());
			}

		}

		Log.LOGGER.info(String.format("Portfolios: { %s }", StringUtils.join(portfolioSet, ", ")));
		Log.LOGGER.info(String.format("Sub-Portfolios: { %s }", StringUtils.join(subPortfolioSet, ", ")));
		Log.LOGGER.info(String.format("Files: { %s }", StringUtils.join(fileSet, ", ")));
		Log.LOGGER.info(String.format("Sub-Files: { %s }", StringUtils.join(subFileSet, ", ")));
		Log.LOGGER.info(String.format("Sub-File-Primary-Suffices: { %s }", StringUtils.join(subFilePrimarySuffixSet, ", ")));
		Log.LOGGER.info(String.format("Sub-File-Secondary-Suffices: { %s }", StringUtils.join(subFileSecondarySuffixSet, ", ")));
		Log.LOGGER.info(String.format("Content: { %s }", StringUtils.join(contentSet, ", ")));
	}

	@Test
	public void recordsMissingInArchiveDatabase() {
		Set<GSACallNumber> missingCallNumbers = new TreeSet<GSACallNumber>();
		for (AllegroRecord record : faustInventory) {
			GSACallNumber callNumber = faustInventory.getCallNumber(record);
			SortedSet<ArchiveDatabaseRecord> matchingRecords = archiveDatabase.filter(callNumber);
			if (matchingRecords.isEmpty()) {
				missingCallNumbers.add(callNumber);
			} else {
				SortedSet<GSACallNumber> matchingCallNumbers = new TreeSet<GSACallNumber>();
				for (ArchiveDatabaseRecord matchingRecord : matchingRecords) {
					matchingCallNumbers.add(matchingRecord.getCallNumber());
				}
				Log.LOGGER.info(String.format("%s ==> { %s }", callNumber, StringUtils.join(matchingCallNumbers, "; ")));
			}
		}

		Log.LOGGER.info(String.format("Missing call numbers: { %s }", StringUtils.join(missingCallNumbers, ", ")));
	}
}
