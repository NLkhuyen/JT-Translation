package com.example.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.app.ApplicationErrorReport.CrashInfo;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Nghia Pham on 7/19/2015.
 */
public class DbHelper extends SQLiteOpenHelper {

	Context mContext;

	private static final String DATABASE_NAME = "jdict.db";
	private static final int DATABASE_VERSION = 1;

	public static final String NV_TABLE = "japanese_vietnamese";
	public static final String NV_COLUMN_ID = "_id";
	public static final String NV_COLUMN_WORD = "Word";
	public static final String NV_COLUMN_CONTENT = "Content";
	public static final String NV_COLUMN_FAV = "IsFavorite";
	public static final String[] NV_COLUMNS = new String[] { NV_COLUMN_ID,
			NV_COLUMN_WORD, NV_COLUMN_CONTENT, NV_COLUMN_FAV };

	public static final String KANJI_TABLE = "Kanji";
	public static final String KANJI_COLUMN_ID = "_id";
	public static final String KANJI_COLUMN_WORD1 = "Word1";
	public static final String KANJI_COLUMN_WORD2 = "Word2";
	public static final String KANJI_COLUMN_CONTENT = "Content";
	public static final String KANJI_COLUMN_FAV = "IsFavorite";
	public static final String[] KANJI_COLUMNS = new String[] {
			KANJI_COLUMN_ID, KANJI_COLUMN_WORD1, KANJI_COLUMN_WORD2,
			KANJI_COLUMN_CONTENT, KANJI_COLUMN_FAV };

	static String DB_PATH;

	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mContext = context;
		DB_PATH = "/data/data/" + mContext.getPackageName() + "/databases/";
		Log.d("DB_URL", DB_PATH);
		try {
			checkDataBase();
			copyDataBase();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean isSpecialChar(char c) {
		switch (c) {
		case '∴':
			return true;
		case '☆':
			return true;
		case '◆':
			return true;
		case '※':
			return true;
		case ':':
			return true;
		default:
			break;
		}
		return false;
	}

	private WordInforTitle getTitle(char c) {
		switch (c) {
		case '∴':
			return WordInforTitle.HAN_VIET;
		case '☆':
			return WordInforTitle.WORD_CLASS;
		case '◆':
			return WordInforTitle.WORD_MEANING;
		case '※':
			return WordInforTitle.EXAMPLE;
		case ':':
			return WordInforTitle.EXAMPLE_MEANING;
		default:
			break;
		}
		return null;
	}

	public ArrayList<Word> getWordMatched(String input) {
		SQLiteDatabase db = getReadableDatabase();
		ArrayList<Word> arr = new ArrayList<Word>();

		// set boundaries for input string
		String upperBound = "";
		if (!input.isEmpty() && input.charAt(0) != '#') {
			char last = input.charAt(input.length() - 1);
			for (int i = 0; i <= input.length() - 2; i++) {
				upperBound += input.charAt(i);
			}
			upperBound += (char) (last + 1);
		} else
			upperBound += input;

		Cursor cursor = db.query(true, NV_TABLE, NV_COLUMNS, NV_COLUMN_WORD
				+ " >= '" + input + "' AND " + NV_COLUMN_WORD + " < '"
				+ upperBound + "'", null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Word word = new Word();
			word.setId(cursor.getInt(0));
			word.setWord(cursor.getString(1));

			String content = cursor.getString(2);
			ArrayList<WordInfor> wordInforArr = new ArrayList<WordInfor>();

			for (int i = 0; i < content.length(); i++) {
				// gap 1 ky tu dac biet va set title cho wordInfor tuong ung
				if (isSpecialChar(content.charAt(i))) {
					WordInforTitle title = getTitle(content.charAt(i));
					int j = i + 1;
					while (j < content.length()) {
						// gap ky tu dac biet tiep theo hoac dau '.' la ket thuc
						// 1 infor
						if (content.charAt(j) == '.'
								|| isSpecialChar(content.charAt(j))) {
							wordInforArr.add(new WordInfor(title, content
									.substring(i + 1, j)));
							break;
						}
						j++;
					}
				}
			}
			word.setWordInfor(wordInforArr);
			word.setIsFavorite(cursor.getInt(3));
			arr.add(word);
			cursor.moveToNext();
		}
		db.close();
		return arr;
	}

	public ArrayList<Kanji> getKanjiMatched(String input) {
		SQLiteDatabase db = getReadableDatabase();
		ArrayList<Kanji> arr = new ArrayList<Kanji>();

		// set boundaries for input string
		String upperBound = "";
		if (!input.isEmpty() && input.charAt(0) != '#') {
			char last = input.charAt(input.length() - 1);
			for (int i = 0; i <= input.length() - 2; i++) {
				upperBound += input.charAt(i);
			}
			upperBound += (char) (last + 1);
		} else
			upperBound += input;

		Cursor cursor = db.query(true, KANJI_TABLE, KANJI_COLUMNS, "("
				+ KANJI_COLUMN_WORD1 + " >= '" + input + "' AND "
				+ KANJI_COLUMN_WORD1 + " < '" + upperBound + "') OR" + "("
				+ KANJI_COLUMN_WORD2 + " = '" + input + "')", null, null, null,
				null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Kanji kanji = new Kanji();

			kanji.setId(cursor.getInt(0));
			kanji.setWord1(cursor.getString(1));
			kanji.setWord2(cursor.getString(2));

			String content = cursor.getString(3);
			ArrayList<KanjiInfor> kanjiInforArr = new ArrayList<KanjiInfor>();
			int i = 0;
			while (i < content.length()) {
				if (content.charAt(i) == '*') {
					kanjiInforArr.add(new KanjiInfor(KanjiInforTitle.ON_YOMI,
							content.substring(0, i)));
					kanjiInforArr.add(new KanjiInfor(KanjiInforTitle.MEANING,
							content.substring(i + 1, content.length())));
					break;
				}
				i++;
			}
			kanji.setContent(kanjiInforArr);
			kanji.setIsFavorite(cursor.getInt(4));

			arr.add(kanji);
			cursor.moveToNext();
		}
		db.close();
		return arr;
	}


	/** This method checks whether database is exists or not **/
	private boolean checkDataBase() {
		try {
			final String mPath = DB_PATH + DATABASE_NAME;
			final File file = new File(mPath);
			if (file.exists()) {
				file.delete();
				Log.d("Delete", "da xoa " + file.getAbsolutePath());
				return false;
			} else
				return false;
		} catch (SQLiteException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * This method will copy database from /assets directory to application
	 * package /databases directory
	 **/
	private void copyDataBase() throws IOException {
		try {
			Log.d("Copying", "dang cop");
			InputStream mInputStream = mContext.getAssets().open(
					"databases/" + DATABASE_NAME);
			String outFileName = DB_PATH + DATABASE_NAME;
			OutputStream mOutputStream = new FileOutputStream(outFileName);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = mInputStream.read(buffer)) > 0) {
				mOutputStream.write(buffer, 0, length);
			}
			mOutputStream.flush();
			mOutputStream.close();
			mInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
}
