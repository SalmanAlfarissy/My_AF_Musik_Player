package com.pnp.myafmusikplayer.model

import java.io.Serializable

class ModelListLagu : Serializable{
    var strId: String? = null

    @JvmField
    var strJudulMusic: String? = null

    @JvmField
    var strNamaBand: String? = null

    @JvmField
    var strCoverLagu: String? = null
}