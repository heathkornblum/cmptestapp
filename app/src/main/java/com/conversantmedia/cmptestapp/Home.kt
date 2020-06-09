package com.conversantmedia.cmptestapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.conversantmedia.gdprcmp.CnvrCmp
import kotlinx.android.synthetic.main.fragment_home.*
import java.io.BufferedReader
import java.io.IOException

private const val TC_STRING = "IABTCF_TCString"
private const val CONFIG_VERSION = "CNVR_PublisherConfigVersion"

class Home : Fragment() {

    private val TAG = javaClass.simpleName
    private var navController: NavController? = null
    private var prefs: SharedPreferences? = null
    lateinit var sharedPreferenceChangeListener: OnSharedPreferenceChangeListener
    private var cmp: CnvrCmp? = null
    private var jsonString: String? = null
    private val OPEN_REQUEST_CODE = 41



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        sharedPreferenceChangeListener = OnSharedPreferenceChangeListener { sharedPreferences: SharedPreferences, s: String ->
            when (s) {
                TC_STRING -> tcStringDisplay.text = sharedPreferences.getString(s, getString(R.string.tcStringNotSet))
                CONFIG_VERSION -> cmpVersionDisplay.text = sharedPreferences.getString(s, getString(R.string.versionNotSet))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navController = findNavController()
        val thisContext = context?.applicationContext
        if (thisContext != null) {
//            val jsonResource = readJSONFile(thisContext, "sampleconfig.json")
            cmp =  CnvrCmp(thisContext)
//            cmp = CnvrCmp(thisContext, jsonResource)
            cmp?.startCmp()
        }

        deleteTCString.setOnClickListener {
            prefs?.edit()?.remove(TC_STRING)?.apply()
        }

        setConfigVersion.setOnClickListener {
            prefs?.edit()?.putString(CONFIG_VERSION, "0")?.apply()
        }

        popUI.setOnClickListener {
            val tcStringPresent:Boolean = prefs?.getString(TC_STRING, "") != ""
            val versionNotZero = prefs?.getString(CONFIG_VERSION, "0") != "0"

            if (tcStringPresent && versionNotZero) {
                Toast.makeText(context, "Unset TC String or Set Version to 0", Toast.LENGTH_SHORT).show()
            }
            // Engage!
            // Git Test Change
            cmp?.startCmp()

        }

        viewTCSharedPrefs.setOnClickListener {
            navController?.navigate(R.id.action_home_to_viewTcfSharedPrefs)
        }



        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        if (jsonString != null) {
            editConfig.text = "Set Config From Manifest"
        }

        editConfig.setOnClickListener {
            if (jsonString == null) {
                openFile(it)
            } else {
                if (context != null) {
                    jsonString = null
                    cmp = CnvrCmp(context as Context)
                    editConfig.text = "Upload a New Config"
                }
            }
        }

        tcStringDisplay.text = prefs?.getString(TC_STRING, getString(R.string.tcStringNotSet))
        cmpVersionDisplay.text = prefs?.getString(CONFIG_VERSION, getString(R.string.versionNotSet))
        prefs?.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)

    }

    override fun onPause() {
        super.onPause()
        prefs?.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
    }

    private fun readJSONFile(context: Context, fileName: String): String? {

        try {
            jsonString = context.assets.open(fileName).bufferedReader().use {
                it.readText()
            }

        } catch (ioException: IOException) {
            Log.e(tag, ioException.message)
            return null
        }
        return jsonString

    }

    private fun openFile(view: View) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/octet-stream"
        startActivityForResult(intent, OPEN_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == OPEN_REQUEST_CODE) {
                data?.let {
                    try {
                        val dataString = it.data.toString()
                        if (dataString.substring(dataString.lastIndexOf(".")) == ".json") {
                            val content = readFile(it.data)
                            content.let { jsonString = content.toString() }
                            cmp = if (context !== null && jsonString != null) {
                                CnvrCmp(context as Context, jsonString as String)
                            } else {
                                CnvrCmp(context as Context)
                            }
                        } else {
                            throw IOException()
                        }
                    } catch (e: IOException) {
                        Log.e(javaClass.simpleName, e.message)
                    }
                }
            }
        }
    }

    private fun readFile(uri: Uri):String? {
        val contentResolver = context?.contentResolver
        val inputStream = contentResolver?.openInputStream(uri)
        val json = inputStream?.bufferedReader()?.use(BufferedReader::readText)
        inputStream?.close()
        return json
    }
}
