package br.iesb.android.opiniaodetudo.fragment

import android.arch.lifecycle.ViewModelProviders
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import br.iesb.android.opiniaodetudo.R
import br.iesb.android.opiniaodetudo.model.Review
import br.iesb.android.opiniaodetudo.model.repository.ReviewRepository
import br.iesb.android.opiniaodetudo.viewmodel.EditReviewViewModel


class EditDialogFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.new_review_form_layout, null)
        populateView(view)
        configureSaveButton(view)
        return view
    }

    private fun configureSaveButton(view: View) {
        val textName = view.findViewById<EditText>(R.id.input_nome)
        val textReview = view.findViewById<EditText>(R.id.input_review)
        val button = view.findViewById<Button>(R.id.button_save)
        val viewModel = ViewModelProviders.of(activity!!).get(EditReviewViewModel::class.java)
        var review: Review = viewModel.data.value!!

        button.setOnClickListener {
//            val review = Review(review.id, textName.text.toString(), textReview.text.toString())
//            ReviewRepository(activity!!.applicationContext).update(review)
//            viewModel.data.value = review
//            this.dismiss()
//        }
//    }
        object: AsyncTask<Void, Void, Unit>(){
            override fun doInBackground(vararg params: Void?) {
                ReviewRepository(activity!!.applicationContext).update(review, textName.text.toString(), textReview.text.toString());
            }

            override fun onPostExecute(result: Unit?) {
                viewModel.data.value = review
                dismiss()
            }

        }.execute()
    }
}

    private fun populateView(view: View) {
        val review = ViewModelProviders.of(activity!!).get(EditReviewViewModel::class.java).data.value
        view.findViewById<EditText>(R.id.input_nome).setText(review!!.name)
        view.findViewById<EditText>(R.id.input_review).setText(review!!.review)
    }

    override fun onResume() {
        val params = dialog.window.attributes.apply {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = ViewGroup.LayoutParams.MATCH_PARENT
        }
        dialog.window.attributes = params
        super.onResume()
    }
}