package me.valour.bowls;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final Button pref = (Button)this.findViewById(R.id.btn_presets);

	}
	
	@Override
	protected void onResume(){
		super.onResume();
		((ImageButton)findViewById(R.id.btn_split_line)).setEnabled(true);
		((ImageButton)findViewById(R.id.btn_split_equally)).setEnabled(true);
	}
	
	private void launchTableActivity(boolean splitEqually){
		Intent intent = new Intent(this, TableActivity.class);
		intent.putExtra("splitEqually", splitEqually);
		startActivity(intent);
	}
	
	public void onSplitEqually(View view){
		ImageButton btnOther = (ImageButton)findViewById(R.id.btn_split_line);
		btnOther.setEnabled(false);
		launchTableActivity(true);
	}
	
	public void onSplitByItem(View view){
		ImageButton btnOther = (ImageButton)findViewById(R.id.btn_split_equally);
		btnOther.setEnabled(false);
		launchTableActivity(false);
	}
	
	public void onOpenPresets(View view){
		Intent intent = new Intent(this, PresetActivity.class);
		startActivity(intent);
	}

}
