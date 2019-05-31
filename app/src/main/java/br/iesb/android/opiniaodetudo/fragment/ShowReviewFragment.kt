package br.iesb.android.opiniaodetudo.fragment

import android.arch.lifecycle.ViewModelProviders
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import br.iesb.android.opiniaodetudo.R
import br.iesb.android.opiniaodetudo.viewmodel.EditReviewViewModel
import java.io.File

class ShowReviewFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.show_review, null).apply {
            val imageView = findViewById<ImageView>(R.id.expandedImage)
            val reviewViewModel = ViewModelProviders.of(activity!!).get(EditReviewViewModel::class.java)
            val value = reviewViewModel?.data?.value
            value?.photoPath?.apply {
                val bitmap = BitmapFactory.decodeFile(File(activity!!.filesDir, this).absolutePath)
                imageView.setImageBitmap(bitmap)
            }
            val toolbar = findViewById<Toolbar>(R.id.toolbar)
            toolbar.title = value?.name
            val activity = (activity!! as AppCompatActivity)
            activity.setSupportActionBar(toolbar)
            activity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            val textView = findViewById<TextView>(R.id.review)
            textView.text = value?.review
        }
    }
}