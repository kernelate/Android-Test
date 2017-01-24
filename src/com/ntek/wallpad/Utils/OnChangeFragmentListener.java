package com.ntek.wallpad.Utils;

import android.app.Fragment;

public interface OnChangeFragmentListener {
	public void changeFragment(Fragment fragList, Fragment fragView, boolean addToBackStack);
}
