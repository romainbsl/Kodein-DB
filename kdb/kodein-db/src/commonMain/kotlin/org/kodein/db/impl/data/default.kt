package org.kodein.db.impl.data

import org.kodein.db.DBFactory
import org.kodein.db.data.DataDB

expect val DataDB.Companion.default: DBFactory<DataDB>
