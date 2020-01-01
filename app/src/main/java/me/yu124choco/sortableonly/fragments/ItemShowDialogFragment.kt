package me.yu124choco.sortableonly.fragments

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import me.yu124choco.sortableonly.R
import me.yu124choco.sortableonly.models.Item

class ItemShowDialogFragment : DialogFragment() {

    var item: Item? = null
    var editButtonClickListener: ((item: Item) -> Unit)? = null
    var deleteButtonClickListener: ((item: Item) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(activity as Context)
        dialog.window?.let {
            it.requestFeature(Window.FEATURE_NO_TITLE)
            it.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
            it.setContentView(R.layout.fragment_item_show_dialog)
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        item?.let { item ->
            dialog.findViewById<TextView>(R.id.text_view_name).text = item.name
            dialog.findViewById<TextView>(R.id.text_view_description).text = item.description
            dialog.findViewById<ImageView>(R.id.image_view_edit_button).setOnClickListener { editButtonClickListener?.invoke(item) }
            dialog.findViewById<ImageView>(R.id.image_view_delete_button).setOnClickListener { deleteButtonClickListener?.invoke(item) }
        }

        return dialog
    }



}