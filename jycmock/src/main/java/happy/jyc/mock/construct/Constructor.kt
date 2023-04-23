package happy.jyc.mock.construct

import okhttp3.Response

abstract class Constructor {
    abstract fun acceptable(): Boolean
    abstract fun construct(): Response
}