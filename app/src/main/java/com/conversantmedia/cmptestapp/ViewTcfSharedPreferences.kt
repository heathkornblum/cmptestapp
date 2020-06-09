package com.conversantmedia.cmptestapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_view_tcf_shared_prefs.*

class viewTcfSharedPrefs : Fragment() {

    private val TAG = javaClass.simpleName
    private var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_tcf_shared_prefs, container, false)
    }

    /**
     * Set the display for each field of this fragment view.  If the Shared Preferences value is
     * a number, use it.  If a number value is not set, use the string Value Not Set
     *
     * String values are displayed or presented as Value Not Set
     *
     * @param key String: A Shared Preferences Key for the file labeled IABTCF
     * @param number Boolean: Is this a number value?
     */
    private fun setDisplayValue(key: String, number: Boolean) : String? {
        if (!number) {
            val stringDisplay = prefs?.getString(key, getString(R.string.valueNotSet))
            return if (stringDisplay == "") {
                getString(R.string.valueNotSet)
            } else {
                stringDisplay
            }
        } else {
            val numberValue = prefs?.getInt(key, -1)
            return if (numberValue != null && numberValue < 0) {
                getString(R.string.valueNotSet)
            } else {
                numberValue.toString()
            }
        }
    }

    private fun getRestrictions() : String {
        val allPrefsMap = prefs?.all
        var displayString = ""
        allPrefsMap?.keys?.forEach {
            if (it.startsWith("IABTCF_PublisherRestrictions", false)) {
                displayString += it.plus(": ")
                    .plus(prefs?.getString(it, getString(R.string.valueNotSet)))
                    .plus("\n")
            }
        }
        return if (displayString == "") getString(R.string.valueNotSet) else displayString
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getRestrictions()
        IABTCF_CmpSdkID_Display.text = setDisplayValue("IABTCF_CmpSdkID", true)
        IABTCF_CmpSdkVersion_Display.text = setDisplayValue("IABTCF_CmpSdkVersion", false)
        IABTCF_PolicyVersion_Display.text = setDisplayValue("IABTCF_PolicyVersion", true)
        IABTCF_gdprApplies_Display.text = setDisplayValue("IABTCF_gdprApplies", true)
        IABTCF_PublisherCC_Display.text = setDisplayValue("IABTCF_PublisherCC", false)
        IABTCF_PurposeOneTreatment_Display.text = setDisplayValue("IABTCF_PurposeOneTreatment", true)
        IABTCF_UseNonStandardStacks_Display.text = setDisplayValue("IABTCF_UseNonStandardStacks", true)
        IABTCF_TCString_Display.text = setDisplayValue("IABTCF_TCString", false)
        IABTCF_VendorConsents_Display.text = setDisplayValue("IABTCF_VendorConsents", false)
        IABTCF_VendorLegitimateInterests_Display.text = setDisplayValue("IABTCF_VendorLegitimateInterests", false)
        IABTCF_PurposeConsents_Display.text = setDisplayValue("IABTCF_PurposeConsents", false)
        IABTCF_PurposeLegitimateInterests_Display.text = setDisplayValue("IABTCF_PurposeLegitimateInterests", false)
        IABTCF_SpecialFeaturesOptIns_Display.text = setDisplayValue("IABTCF_SpecialFeaturesOptIns", false)
        IABTCF_PublisherRestrictions_Display.text = getRestrictions()
        IABTCF_PublisherConsent_Display.text = setDisplayValue("IABTCF_PublisherConsent", false)
        IABTCF_PublisherLegitimateInterests_Display.text = setDisplayValue("IABTCF_PublisherLegitimateInterests", false)
        IABTCF_PublisherCustomPurposesConsents_Display.text = setDisplayValue("IABTCF_PublisherCustomPurposesConsents", false)
        IABTCF_PublisherCustomPurposesLegitimateInterests_Display.text = setDisplayValue("IABTCF_PublisherCustomPurposesLegitimateInterests", false)

        super.onViewCreated(view, savedInstanceState)
    }

}
