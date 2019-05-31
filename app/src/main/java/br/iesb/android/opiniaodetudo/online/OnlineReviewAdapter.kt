package br.iesb.android.opiniaodetudo.online

import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.support.v7.widget.RecyclerView
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import br.iesb.android.opiniaodetudo.R
import br.iesb.android.opiniaodetudo.model.Review
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject

class OnlineReviewAdapter : RecyclerView.Adapter<OnlineReviewAdapter.ReviewViewHolder> {
    constructor() : super(){
        loadList()
    }
    private var list: MutableList<Review> = mutableListOf()
    fun loadList() {
        loadList { }
    }
    fun loadList(callback: ()->Unit) {
        object: AsyncTask<Void, Void, Unit>(){
            override fun doInBackground(vararg params: Void?) {
                val okHttpClient = OkHttpClient()
                val request = Request.Builder()
                    .url("$BASE_URL/$REVIEWS_URI")
                    .build()
                val response = okHttpClient.newCall(request).execute()
                val jsonList = JSONArray(response.body()!!.string())
                this@OnlineReviewAdapter.list = mutableListOf<Review>()
                for(i in 1..jsonList.length()){
                    val jsonObject = jsonList[i-1] as JSONObject
                    val review = jsonObject.toReview()
                    list.add(review)
                }
            }
            override fun onPostExecute(result: Unit?) {
                this@OnlineReviewAdapter.notifyDataSetChanged()
                callback()
            }
        }.execute()
    }

    override fun getItemCount(): Int = this.list.size

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        if(this.list != null && !this.list.isEmpty()){
            (holder.reviewItemView as ViewGroup).apply {
                val review = this@OnlineReviewAdapter.list[position]
                findViewById<TextView>(R.id.item_name).text = review.name
                findViewById<TextView>(R.id.item_review).text = review.review
                val thumbnail = findViewById<ImageView>(R.id.thumbnail)
                if (review.thumbnail != null) {
                    val bitmap = BitmapFactory
                        .decodeByteArray(review.thumbnail, 0, review.thumbnail.size)
                    thumbnail.setImageBitmap(bitmap)
                }else{
                    thumbnail.setImageResource(R.drawable.placeholder)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.review_list_item_layout, null)
        return ReviewViewHolder(itemView)
    }

    class ReviewViewHolder(val reviewItemView: View) : RecyclerView.ViewHolder(reviewItemView)

}

private fun JSONObject.toReview(): Review {
    return Review(
        id=this.getString("id"),
        name=this.getString("name"),
        review=this.getString("review"),
        photoPath = null,
        thumbnail =
        if(this.has("thumbnail")) this.getString("thumbnail").fromBase64() else null,
        latitude =
        if(this.has("latitude")) this.getDouble("latitude") else null,
        longitude =
        if(this.has("longitude")) this.getDouble("longitude") else null
    )
}

private fun String.fromBase64(): ByteArray? {
    return Base64.decode(this, Base64.DEFAULT)
}


