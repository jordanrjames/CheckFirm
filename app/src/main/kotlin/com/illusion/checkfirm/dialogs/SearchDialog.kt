package com.illusion.checkfirm.dialogs

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.browser.trusted.TrustedWebActivityIntentBuilder
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.google.androidbrowserhelper.trusted.TwaLauncher
import com.illusion.checkfirm.R
import com.illusion.checkfirm.etc.WebViewActivity
import com.illusion.checkfirm.etc.SherlockActivity
import com.illusion.checkfirm.utils.Tools

class SearchDialog : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.dialog_search, container, false)

        val smart = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("smart", true)

        val isOfficial = requireArguments().getBoolean("isOfficial")
        val model = requireArguments().getString("model")
        val csc = requireArguments().getString("csc")
        val officialLatest = requireArguments().getString("official_latest").toString()
        val testLatest = requireArguments().getString("test_latest").toString()

        val latestTitle = rootView.findViewById<MaterialTextView>(R.id.latest_title)
        val latestText = rootView.findViewById<MaterialTextView>(R.id.latest_firmware)
        val changelog = rootView.findViewById<MaterialButton>(R.id.changelog)
        val copy = rootView.findViewById<MaterialButton>(R.id.copy)
        val share = rootView.findViewById<MaterialButton>(R.id.share)
        val previousTitle = rootView.findViewById<MaterialTextView>(R.id.previous_title)
        val previousList = rootView.findViewById<MaterialTextView>(R.id.list)

        val smartSearch = rootView.findViewById<MaterialCardView>(R.id.smart_search)
        val bootloader = rootView.findViewById<MaterialTextView>(R.id.bootloader)
        val majorVersion = rootView.findViewById<MaterialTextView>(R.id.major_version)
        val date = rootView.findViewById<MaterialTextView>(R.id.date)
        val minorVersion = rootView.findViewById<MaterialTextView>(R.id.minor_version)
        val sherlock = rootView.findViewById<MaterialCardView>(R.id.sherlock)

        if (isOfficial) {
            latestTitle.text = getString(R.string.latest_official)
            latestText.text = officialLatest
            previousTitle.text = getString(R.string.previous_official)
            previousList.text = requireArguments().getString("official_previous")
            changelog.setOnClickListener {
                val link = "http://doc.samsungmobile.com/$model/$csc/doc.html"
                try {
                    val builder = TrustedWebActivityIntentBuilder(Uri.parse(link))
                    TwaLauncher(requireActivity()).launch(builder, null, null)
                } catch (e: ActivityNotFoundException) {
                    val intent = Intent(requireActivity(), WebViewActivity::class.java)
                    intent.putExtra("url", link)
                    intent.putExtra("number", 1)
                    startActivity(intent)
                }
            }

            if (officialLatest.isNotBlank() && officialLatest != getString(R.string.search_error)) {
                if (smart) {
                    smartSearch.visibility = View.VISIBLE

                    val firmwareInfo = Tools.getFirmwareInfo(officialLatest)
                    bootloader.text = firmwareInfo.substring(0, 2)
                    majorVersion.text = firmwareInfo.substring(2, 3)
                    date.text = getFirmwareDate(firmwareInfo.substring(3, 5))
                    minorVersion.text = firmwareInfo.substring(5, 6)
                } else {
                    smartSearch.visibility = View.GONE
                }
            } else {
                smartSearch.visibility = View.GONE
            }
            sherlock.visibility = View.GONE
        } else {
            latestTitle.text = getString(R.string.latest_test)
            latestText.text = testLatest
            previousTitle.text = getString(R.string.previous_test)
            previousList.text = requireArguments().getString("test_previous")
            changelog.visibility = View.GONE

            if (officialLatest.isNotBlank() && officialLatest != getString(R.string.search_error)
                    && testLatest.isNotBlank() && testLatest != getString(R.string.search_error)) {
                if (testLatest.contains("/")) {
                    if (smart) {
                        smartSearch.visibility = View.VISIBLE

                        val firmwareInfo = Tools.getFirmwareInfo(testLatest)
                        bootloader.text = firmwareInfo.substring(0, 2)
                        majorVersion.text = firmwareInfo.substring(2, 3)
                        date.text = getFirmwareDate(firmwareInfo.substring(3, 5))
                        minorVersion.text = firmwareInfo.substring(5, 6)
                    } else {
                        smartSearch.visibility = View.GONE
                    }
                    sherlock.visibility = View.GONE
                } else {
                    smartSearch.visibility = View.GONE
                    sherlock.visibility = View.VISIBLE
                    sherlock.setOnClickListener {
                        val intent = Intent(requireActivity(), SherlockActivity::class.java)
                        intent.putExtra("official", officialLatest)
                        intent.putExtra("firmware", testLatest)
                        startActivity(intent)
                    }
                }
            } else {
                smartSearch.visibility = View.GONE
                sherlock.visibility = View.GONE
            }
        }

        val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        copy.setOnClickListener {
            val clip = ClipData.newPlainText("checkfirmLatest", latestText.text.toString())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireActivity(), R.string.clipboard, Toast.LENGTH_SHORT).show()
        }

        share.setOnClickListener {
            val link = "https://checkfirm.com/$model/$csc"
            val clip = ClipData.newPlainText("checkfirmLink", link)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireActivity(), R.string.link_shared, Toast.LENGTH_SHORT).show()
        }

        val okButton = rootView.findViewById<MaterialButton>(R.id.ok)
        okButton.setOnClickListener {
            dismiss()
        }

        return rootView
    }

    private fun getFirmwareDate(date: String): String {
        val year = when (date.substring(0, 1)) {
            "A" -> getString(R.string.year_a)
            "B" -> getString(R.string.year_b)
            "C" -> getString(R.string.year_c)
            "D" -> getString(R.string.year_d)
            "E" -> getString(R.string.year_e)
            "F" -> getString(R.string.year_f)
            "G" -> getString(R.string.year_g)
            "H" -> getString(R.string.year_h)
            "I" -> getString(R.string.year_i)
            "J" -> getString(R.string.year_j)
            "K" -> getString(R.string.year_k)
            "L" -> getString(R.string.year_l)
            "M" -> getString(R.string.year_m)
            "N" -> getString(R.string.year_n)
            "O" -> getString(R.string.year_o)
            "P" -> getString(R.string.year_p)
            "Q" -> getString(R.string.year_q)
            "R" -> getString(R.string.year_r)
            "S" -> getString(R.string.year_s)
            "T" -> getString(R.string.year_t)
            "U" -> getString(R.string.year_u)
            "V" -> getString(R.string.year_v)
            "W" -> getString(R.string.year_w)
            "X" -> getString(R.string.year_x)
            "Y" -> getString(R.string.year_y)
            "Z" -> getString(R.string.year_z)
            else -> getString(R.string.unknown)
        }
        val month = when (date.substring(1, 2)) {
            "A" -> getString(R.string.january)
            "B" -> getString(R.string.february)
            "C" -> getString(R.string.march)
            "D" -> getString(R.string.april)
            "E" -> getString(R.string.may)
            "F" -> getString(R.string.june)
            "G" -> getString(R.string.july)
            "H" -> getString(R.string.august)
            "I" -> getString(R.string.september)
            "J" -> getString(R.string.october)
            "K" -> getString(R.string.november)
            "L" -> getString(R.string.december)
            else -> getString(R.string.unknown)
        }

        return String.format(getString(R.string.smart_search_date_format), date, year, month)
    }

    companion object {
        fun newInstance(isOfficial: Boolean, model: String, csc: String,
                        officialLatest: String, officialPrevious: String, testLatest: String, testPrevious: String): SearchDialog {
            val f = SearchDialog()

            val args = Bundle()
            args.putBoolean("isOfficial", isOfficial)
            args.putString("model", model)
            args.putString("csc", csc)
            args.putString("official_latest", officialLatest)
            args.putString("official_previous", officialPrevious)
            args.putString("test_latest", testLatest)
            args.putString("test_previous", testPrevious)
            f.arguments = args

            return f
        }
    }
}