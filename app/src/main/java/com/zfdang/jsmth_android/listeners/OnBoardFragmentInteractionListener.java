package com.zfdang.jsmth_android.listeners;

import com.zfdang.jsmth_android.models.Board;

/**
 * Created by zfdang on 2016-3-22.
 * shared by FavoriteBoardFragment & AllBoardFragment, so we move it out
 */
public interface OnBoardFragmentInteractionListener {

  void onBoardFragmentInteraction(Board item);

  void onBoardLongClick(Board item);
}
