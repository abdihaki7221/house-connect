package com.example.houseconnect.employeeModule

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.houseconnect.R
import com.example.houseconnect.authModule.SignIn
import com.example.houseconnect.employeeModule.response.ActivateResponse
import com.example.houseconnect.employerModule.EmployerDashboard
import com.example.houseconnect.retrofit.RetrofitClient
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.Locale


class EditProfile : AppCompatActivity() {
    private val STORAGE_PERMISSION_REQUEST_CODE = 101
    private var frontImageUri: Uri? = null
    private var backImageUri: Uri? = null
    private lateinit var locationAutoComplete: AutoCompleteTextView
    private lateinit var locationAdapter: ArrayAdapter<String>
    private lateinit var locationList: List<String>

    // ActivityResultLauncher for selecting front image
    private val galleryImageFrontLauncher: ActivityResultLauncher<Intent?> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    frontImageUri = uri
                    handleImageSelection(uri, R.id.front_image_name)
                }
            }
        }

    // ActivityResultLauncher for selecting back image
    private val galleryImageBackLauncher: ActivityResultLauncher<Intent?> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    backImageUri = uri
                    handleImageSelection(uri, R.id.back_image_name)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        locationAutoComplete = findViewById(R.id.location_auto_complete)

        // Load your JSON data from the same folder as YourActivity and extract location names into a List<String>.
        locationList = loadLocationsFromJSON(this)
        locationAdapter = ArrayAdapter(this, R.layout.simpple_dropdown_layout, locationList)
        locationAutoComplete.setAdapter(locationAdapter)



        locationAutoComplete.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used in this case.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Filter the suggestions based on the user's input.
                val userInput = s.toString().lowercase(Locale.ROOT)
                val filteredSuggestions = locationList.filter { it.lowercase(Locale.ROOT).contains(userInput) }
                locationAdapter.clear()
                locationAdapter.addAll(filteredSuggestions)
                locationAdapter.notifyDataSetChanged()
            }

            override fun afterTextChanged(s: Editable?) {
                // Not used in this case.
            }
        })



//        // Initialize spinners and adapters
//        val data = arrayOf("South C", "Eastleigh", "South B", "Pangani", "Westland")
        val jobTypeData = arrayOf("part-time", "Full-Time", "HouseChores")

//        val spinner = findViewById<Spinner>(R.id.select_residence)
        val jobType = findViewById<Spinner>(R.id.select_job_type)

//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, data)
        val adapterJobType = ArrayAdapter(this, android.R.layout.simple_spinner_item, jobTypeData)

//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapterJobType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

//        spinner.adapter = adapter
        jobType.adapter = adapterJobType

        val backBtn = findViewById<TextView>(R.id.back_btn)
        backBtn.setOnClickListener{
            startActivity(Intent(this@EditProfile, EmployeeDashboard::class.java))
            finish()
        }

        // Set up button click listeners
        val uploadFrontImageButton = findViewById<Button>(R.id.uploadFrontImageButton)
        val uploadBackImageButton = findViewById<Button>(R.id.uploadBackImageButton)
        val activateAccountButton = findViewById<Button>(R.id.activate_account)

        uploadFrontImageButton.setOnClickListener {
            checkStoragePermissionAndOpenGallery(galleryImageFrontLauncher)
        }

        uploadBackImageButton.setOnClickListener {
            checkStoragePermissionAndOpenGallery(galleryImageBackLauncher)
        }

        activateAccountButton.setOnClickListener {

            val idNumberText = findViewById<EditText>(R.id.id_txt).text.toString()
            val residenceText = locationAutoComplete.toString().trim()
            val jobTypeText = jobType.selectedItem.toString()

            val idNumber = idNumberText.toRequestBody("text/plain".toMediaTypeOrNull())
            val relProfile = findViewById<RelativeLayout>(R.id.activate_rel_layout)
            val pendProfile = findViewById<RelativeLayout>(R.id.pending_rel_layout)
            val residence = residenceText.toRequestBody("text/plain".toMediaTypeOrNull())
            val jobTypes = jobTypeText.toRequestBody("text/plain".toMediaTypeOrNull())
            if (idNumberText.isNotEmpty() && residenceText.isNotEmpty() && jobTypeText.isNotEmpty()) {

                if (frontImageUri != null && backImageUri != null) {
                    val frontImagePart = frontImageUri!!.toMultipartBodyPart(contentResolver, "frontImage")
                    val backImagePart = backImageUri!!.toMultipartBodyPart(contentResolver, "backImage")

                    val sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
                    val cacheToken = sharedPreferences.getString("token", null)

                    if (cacheToken != null) {
                        val apiService = RetrofitClient.create(cacheToken)
//                        val request: ActivateRequest = ActivateRequest()
                        if (frontImagePart != null && backImagePart != null) {
                            // Your API call here
                            val call = apiService.activateEmployee(
                                idNumber, jobTypes, residence, frontImagePart, backImagePart
                            )

                            call.enqueue(object : Callback<ActivateResponse> {
                                override fun onResponse(call: Call<ActivateResponse>, response: Response<ActivateResponse>) {
                                    if (response.isSuccessful) {
                                        val actResponse = response.body()
                                        if (actResponse?.responseCode.equals("00")) {
                                            val message = actResponse?.response
                                            if (message != null) {
                                                //which one is much better and easier to do?
                                                //upon activation request success should the button be disabled or the entire
                                                //activate layout be invisible
                                                startActivity(Intent(this@EditProfile, EmployeeDashboard::class.java))
                                                setResult(RESULT_OK, intent)
                                                finish()

//                                                relProfile.visibility= View.GONE
//                                                pendProfile.visibility = View.VISIBLE

//                                                showErrorUI(message)
                                            }
                                        } else {
                                            val errorMessage = actResponse?.response ?: "Activation failed. Please try again."
                                            showErrorUI(errorMessage)
                                        }
                                    } else {
                                        showErrorUI("Request Failed ->2xx")
                                    }
                                }

                                override fun onFailure(call: Call<ActivateResponse>, t: Throwable) {
                                    t.printStackTrace()
                                    showErrorUI("Request Failed ${t.message}")
                                }
                            })
                        } else {
                            showErrorUI("Front or back image is missing")
                        }


                    } else {
                        val intent = Intent(this@EditProfile, SignIn::class.java)
                        startActivity(intent)
                    }
                } else {
                    showErrorUI("Front or back image is missing")
                }
            } else {
                showErrorUI("Please fill in all required fields")
            }
        }

    }

    private fun loadLocationsFromJSON(context: Context): List<String> {
        val locationList = ArrayList<String>()

        try {
            val inputStream: InputStream = context.assets.open("location.json")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val jsonStringBuilder = StringBuilder()

            reader.forEachLine { line ->
                jsonStringBuilder.append(line)
            }

            val jsonObject = Gson().fromJson(jsonStringBuilder.toString(), JsonObject::class.java)
            if (jsonObject.has("locations") && jsonObject["locations"].isJsonArray) {
                val jsonArray = jsonObject["locations"].asJsonArray
                for (jsonElement in jsonArray) {
                    locationList.add(jsonElement.asString)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return locationList
    }



    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    private fun showErrorUI(message: String) {
        val snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
        snackbar.show()
    }
    @SuppressLint("Recycle")
    fun Uri.toMultipartBodyPart(contentResolver: ContentResolver, fieldName: String, contentType: String = "image/jpeg"): MultipartBody.Part? {
        val inputStream: InputStream? = contentResolver.openInputStream(this)

        if (inputStream != null) {
            val byteArray = inputStream.readBytes()
            val requestBody = byteArray.toRequestBody(contentType.toMediaTypeOrNull())
            return MultipartBody.Part.createFormData(fieldName, getFileName(contentResolver, this), requestBody)
        }

        return null
    }



    private fun getRealPathFromURI(uri: Uri): String? {
        val context: Context = applicationContext

        return if (DocumentsContract.isDocumentUri(context, uri)) {
            // Handle DocumentProvider
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":").toTypedArray()
            if (split.size == 2) {
                val contentUri = when (split[0]) {
                    "com.android.providers.downloads.documents" -> {
                        Uri.parse("content://downloads/public_downloads")
                    }
                    "com.android.providers.media.documents" -> {
                        Uri.parse("content://media/external")
                    }
                    else -> {
                        // Add more cases if needed
                        null
                    }
                }

                contentUri?.let {
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(split[1])
                    getDataColumn(context, it, selection, selectionArgs)
                }
            } else {
                null
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            // Return the remote address
            if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            // Return the actual file path
            uri.path
        } else {
            null
        }
    }

    private fun getDataColumn(context: Context, uri: Uri, selection: String?, selectionArgs: Array<String>?): String? {
        val column = "_data"
        val projection = arrayOf(column)

        context.contentResolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(columnIndex)
            }
        }
        return null
    }

    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }



    private fun checkStoragePermissionAndOpenGallery(activityResultLauncher: ActivityResultLauncher<Intent?>) {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            openGallery(activityResultLauncher)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(permission), STORAGE_PERMISSION_REQUEST_CODE)
        }
    }


    private fun openGallery(activityResultLauncher: ActivityResultLauncher<Intent?>) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        activityResultLauncher.launch(intent)
    }

    private fun handleImageSelection(uri: Uri, textViewId: Int) {
        val imageFileName = getFileName(contentResolver, uri)
        if (imageFileName.length>6){
           val displayedName = imageFileName.substring(0,5)+"..."
            findViewById<TextView>(textViewId).text = displayedName
        }else{
            findViewById<TextView>(textViewId).text = imageFileName
        }

    }

    @SuppressLint("Range")
    private fun getFileName(contentResolver: ContentResolver, uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.moveToFirst()
        val displayName = cursor?.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        cursor?.close()
        return displayName ?: "Unknown"
    }
}
