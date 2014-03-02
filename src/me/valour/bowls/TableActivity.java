package me.valour.bowls;

import java.util.List;

import me.valour.bowls.enums.OkMode;

import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class TableActivity extends Activity 
	implements TableFragment.AddBowlListener, 
	TableFragment.SubBowlListener,
	TableFragment.OkListener{

	private int bowlsCount;
	private Bill bill;
	private FragmentManager fm;
	
	private TableFragment tableFragment;
	private NumberPadFragment numFragment;
	public boolean splitEqually;
	private OkMode okMode;
	private LineItem selectedLineItem=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		splitEqually = intent.getBooleanExtra("splitEqually", true);
		
		bowlsCount = Kitchen.minBowls;
		okMode = OkMode.ITEM_PRICE;
		
		setContentView(R.layout.activity_table);
		
		fm = getFragmentManager();
		tableFragment = (TableFragment) fm.findFragmentById(R.id.tableFragment);
		numFragment = (NumberPadFragment) fm.findFragmentById(R.id.numpadFragment);
		
		bill = new Bill(splitEqually);
		if(splitEqually){
			initSplitEqually();
		} else {
			initSplitLineItems();
		}
		tableFragment.tvQuestion.bringToFront();
		bill.addUniqueUsers(tableFragment.bowlsGroup.getBowlUsers());
	}
	
	private void initSplitEqually(){
		tableFragment.tvQuestion.setText(R.string.q_enter_subtotal);
	}
	
	private void initSplitLineItems(){
		tableFragment.tvQuestion.setText(R.string.q_enter_first_li);
	}

	@Override
	public void OnAddBowlListener() {
		if(bowlsCount==Kitchen.maxBowls){
			
		} else {
			bowlsCount++;
		//	BowlView bowl = tableFragment.tableView.addBowl();
			BowlView bowl = tableFragment.bowlsGroup.addBowl();
			bill.addUser(bowl.user);
			if(splitEqually){
				bill.redivideEqually();
			}
		}
		Log.d("vars",String.format("bowls=%d",bowlsCount));
	}

	@Override
	public void OnSubBowlListener() {
	/*	deprecate
	 * if(bowlsCount==Kitchen.minBowls){
			
		} else {
			bowlsCount--;
			tableFragment.tableView.subBowl();
		}
		Log.d("vars",String.format("bowls=%d",bowlsCount));
		*/
	}

	@Override
	public void OnOkButtonPress() {
		    	switch(okMode){
				case ITEM_PRICE:
					registerItemPrice();
					break;
				case SELECT_BOWLS:
					handleSelectedBowls();
					break;
				default:
					break;
			}
	}
	
	private void registerItemPrice(){
		double price = numFragment.getNumberValue();
		LineItem li = bill.addLineItem(price);
		if(splitEqually){
			bill.divideEqually(li);
			okMode = OkMode.NONE; 
	    	tableFragment.btnOk.setVisibility(View.INVISIBLE);
	    	tableFragment.tvQuestion.setText("");
	    	tableFragment.tvQuestion.setVisibility(View.INVISIBLE);
		} else {
			tableFragment.tvQuestion.setText(R.string.q_select_bowls);
			prepareForSelectingBowls(li);
		}
		tableFragment.bowlsGroup.refreshBowls();
	}
	
	private void prepareForSelectingBowls(LineItem li){
		selectedLineItem = li;
		tableFragment.bowlsGroup.readyBowlSelect();
		okMode = OkMode.SELECT_BOWLS;
	}
	
	private void handleSelectedBowls(){
		if(selectedLineItem!=null){
			List<User> consumers = tableFragment.bowlsGroup.getSelectedUsers();
			bill.divideAmongst(selectedLineItem, consumers);
			
			tableFragment.bowlsGroup.stopBowlSelect();
			
			// move to being ready for next Item
			tableFragment.tvQuestion.setText(R.string.q_enter_next_li);
			numFragment.clearField();
			okMode = OkMode.ITEM_PRICE;
			selectedLineItem = null;
		}
		tableFragment.bowlsGroup.refreshBowls();
	}

}
