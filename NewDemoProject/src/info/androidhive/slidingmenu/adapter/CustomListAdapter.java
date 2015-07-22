package info.androidhive.slidingmenu.adapter;

import java.util.ArrayList;

import com.example.database.Word;
import com.javacodegeeks.androidcameraexample2.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomListAdapter extends ArrayAdapter<Word> {

	LayoutInflater inflater;
	Context context;
	ArrayList<Word> arr = new ArrayList<Word>();

	public CustomListAdapter(Context context, LayoutInflater inflater,
			ArrayList<Word> arr) {
		super(context, R.layout.drawer_list_item, arr);
		// TODO Auto-generated constructor stub
		this.inflater = inflater;
		this.context = context;
		this.arr = arr;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.drawer_list_item, null);
		}
		
		((TextView) convertView.findViewById(R.id.title))
				.setText(arr.get(position).getWord());
		
		// Resets the toolbar to be closed
		View toolbar = convertView.findViewById(R.id.toolbar_listview);
		TextView tv = (TextView) convertView.findViewById(R.id.doSomething1);
		((LinearLayout.LayoutParams) toolbar.getLayoutParams()).bottomMargin = -50;
		toolbar.setVisibility(View.GONE);
		tv.setText(arr.get(position).getWordInfor().toString());

		return convertView;
	}
	
	public void updateAdapter(ArrayList<Word> arrylst) {
		arr.clear();
        this.arr.addAll(arrylst);
        

        //and call notifyDataSetChanged
        notifyDataSetChanged();
    }

}
