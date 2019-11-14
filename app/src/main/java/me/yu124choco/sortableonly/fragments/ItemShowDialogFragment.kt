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
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        dialog.window?.setContentView(R.layout.fragment_item_show_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val textViewName = dialog.findViewById<TextView>(R.id.text_view_name)
        val textViewDescription = dialog.findViewById<TextView>(R.id.text_view_description)
        val imageViewEdit = dialog.findViewById<ImageView>(R.id.image_view_edit_button)
        val imageViewDelete = dialog.findViewById<ImageView>(R.id.image_view_delete_button)

        textViewName.text = item?.name
        textViewDescription.text = item?.description
        imageViewEdit.setOnClickListener {
            if (item != null) editButtonClickListener?.invoke(item!!)
        }
        imageViewDelete.setOnClickListener {
            if (item != null) deleteButtonClickListener?.invoke(item!!)
        }

        return dialog
    }



}