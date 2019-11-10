package me.yu124choco.sortableonly.util

import android.content.Context
import android.widget.Toast

fun makeShortToast(context: Context, text: String) = Toast.makeText(context, text, Toast.LENGTH_SHORT).show()

fun makeLongToast(context: Context, text: String) = Toast.makeText(context, text, Toast.LENGTH_LONG).show()