package br.iesb.android.opiniaodetudo.fragment

import android.app.Activity.*
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import br.iesb.android.opiniaodetudo.R
import br.iesb.android.opiniaodetudo.activity.MainActivity
import br.iesb.android.opiniaodetudo.model.Review
import br.iesb.android.opiniaodetudo.model.repository.ReviewRepository
import br.iesb.android.opiniaodetudo.service.LocationService
import com.google.android.gms.common.util.IOUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class FormFragment : Fragment(){
    private lateinit var mainView: View

    private var thumbnailBytes: ByteArray? = null

    companion object {
        val TAKE_PICTURE_RESULT = 101
        val NEW_REVIEW_MESSAGE_ID = 4584
    }
    private var file: File? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        mainView = inflater.inflate(R.layout.new_review_form_layout, null)
        val buttonSave = mainView.findViewById<Button>(R.id.button_save)
        val textViewName = mainView.findViewById<TextView>(R.id.input_nome)
        val textViewReview = mainView.findViewById<TextView>(R.id.input_review)

        val reviewToEdit = (activity!!.intent?.getSerializableExtra("item") as Review?)?.also { review ->
            textViewName.text = review.name
            textViewReview.text = review.review
            return mainView
        }
        buttonSave.setOnClickListener {
            val name = textViewName.text
            val review = textViewReview.text
            object : AsyncTask<Void, Void, Review>() {
                override fun doInBackground(vararg params: Void?): Review {
                    val repository = ReviewRepository(activity!!.applicationContext)
                    var entity: Review
                    if (reviewToEdit == null) {
                        entity = repository.save(name.toString(),
                            review.toString(),
                            file?.toRelativeString(activity!!.filesDir), thumbnailBytes)
                    } else {
                        entity = repository.update(reviewToEdit.id, name.toString(), review.toString())
                    }
                    (activity as MainActivity).navigateTo(MainActivity.LIST_FRAGMENT)
                    return entity
                }
                override fun onPostExecute(result: Review) {
                    updateReviewLocation(result)
                    showReviewNotification(result)
                }
            }.execute()
            true
        }

        configurePhotoClick()
        handleImageShare()
        return mainView
    }

    private fun configurePhotoClick() {
        mainView.findViewById<ImageView>(R.id.photo).setOnClickListener {
            val fileName = "${System.nanoTime()}.jpg"
            file = File(activity!!.filesDir, fileName)
            val uri = FileProvider.getUriForFile(activity!!,
                "br.iesb.android.opiniaodetudo.fileprovider", file!!)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            startActivityForResult(intent, TAKE_PICTURE_RESULT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == TAKE_PICTURE_RESULT){
            if(resultCode == RESULT_OK){
                val photoView = mainView.findViewById<ImageView>(R.id.photo)
                val bitmap = BitmapFactory.decodeStream(FileInputStream(file))
                val targetSize = 100
                val thumbnail = ThumbnailUtils.extractThumbnail(
                    bitmap,
                    targetSize,
                    targetSize
                )
                photoView.setImageBitmap(bitmap)

                generateThumbnailBytes(thumbnail, targetSize)
            }else{
                Toast.makeText(activity, "Erro ao tirar a foto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateThumbnailBytes(thumbnail: Bitmap, targetSize: Int) {
        val thumbnailOutputStream = ByteArrayOutputStream()
        thumbnail.compress(Bitmap.CompressFormat.PNG, targetSize, thumbnailOutputStream)
        thumbnailBytes = thumbnailOutputStream.toByteArray()
    }

    private fun updateReviewLocation(entity: Review) {
        LocationService(activity!!).onLocationObtained{ lat, long ->
            val repository = ReviewRepository(activity!!.applicationContext)
            object: AsyncTask<Void, Void, Unit>() {
                override fun doInBackground(vararg params: Void?) {
                    repository.updateLocation(entity, lat, long)
                }
            }.execute()
        }
    }

    private fun showReviewNotification(review: Review) {

        val builder = NotificationCompat.Builder(
            activity!!,
            MainActivity.PUSH_NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_android_black_24dp)
            .setContentTitle("Nova Opinião")
            .setContentText(review.name)

        if(review.thumbnail != null){
            val thumbnail =
                BitmapFactory
                    .decodeByteArray(review.thumbnail, 0, review.thumbnail!!.size)
            val photo =
                BitmapFactory
                    .decodeFile(File(activity!!.filesDir, review.photoPath).absolutePath)
            builder.setLargeIcon(thumbnail)
            builder.setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(photo)
                    .bigLargeIcon(null)
            )
        }else{
            builder.setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle(review.name)
                    .bigText(review.review)
            )
        }

        //Ação de deletar na notificação
        val deletePendingIntent = createDeletePendingIntent(review)
        builder.addAction(0, "Apagar", deletePendingIntent)

        NotificationManagerCompat
            .from(activity!!).notify(NEW_REVIEW_MESSAGE_ID, builder.build())
    }

    private fun createDeletePendingIntent(review: Review) : PendingIntent {
        val deleteIntent = Intent(activity!!, MainActivity::class.java)
        deleteIntent.action = MainActivity.DELETE_NOTIFICATION_ACTION_NAME
        deleteIntent.putExtra(MainActivity.DELETE_NOTIFICATION_EXTRA_NAME, review.id)

        return PendingIntent.getActivity(
            activity!!,
            MainActivity.NEW_REVIEW_NOTIFICATION_MESSAGE_REQUEST,
            deleteIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    fun handleImageShare() {
        val intentParam = activity!!.intent
        if(intentParam?.action == Intent.ACTION_SEND) {
            intentParam?.extras.get(Intent.EXTRA_SUBJECT)?.let {
                mainView.findViewById<EditText>(R.id.input_nome).setText(it as String)
            }
            intentParam?.extras.get(Intent.EXTRA_TEXT)?.let {
                mainView.findViewById<EditText>(R.id.input_review).setText(it as String)
            }
            intentParam?.extras.get(Intent.EXTRA_STREAM)?.let {
                val fileName = "${System.nanoTime()}.jpg"
                file = File(activity!!.filesDir, fileName)
                IOUtils.copyStream(
                    activity!!.contentResolver.openInputStream(it as Uri),
                    FileOutputStream(file)
                )
                val photoView = mainView.findViewById<ImageView>(R.id.photo)
                val bitmap = BitmapFactory.decodeStream(FileInputStream(file))
                val targetSize = 100
                val thumbnail = ThumbnailUtils.extractThumbnail(
                    bitmap,
                    targetSize,
                    targetSize
                )
                photoView.setImageBitmap(thumbnail)
                generateThumbnailBytes(thumbnail, targetSize)
            }
        }
    }
}

//    fun handleImageShare() {
//        val intentParam = activity!!.intent
//        if(intentParam?.action == Intent.ACTION_SEND) {
//            intentParam?.extras.get(Intent.EXTRA_SUBJECT)?.let {
//                mainView.findViewById<EditText>(R.id.input_nome).setText(it as String)
//            }
//            intentParam?.extras.get(Intent.EXTRA_TEXT)?.let {
//                mainView.findViewById<EditText>(R.id.input_review).setText(it as String)
//            }
//            intentParam?.extras.get(Intent.EXTRA_STREAM)?.let {
//                val fileName = "${System.nanoTime()}.jpg"
//                file = File(activity!!.filesDir, fileName)
//                IOUtils.copyStream(
//                    activity!!.contentResolver.openInputStream(it as Uri),
//                    FileOutputStream(file)
//                )
//                val photoView = mainView.findViewById<ImageView>(R.id.photo)
//                val bitmap = BitmapFactory.decodeStream(FileInputStream(file))
//                val targetSize = 100
//                val thumbnail = ThumbnailUtils.extractThumbnail(
//                    bitmap,
//                    targetSize,
//                    targetSize
//                )
//                photoView.setImageBitmap(thumbnail)
//                generateThumbnailBytes(thumbnail, targetSize)
//            }
//        }
//    }

