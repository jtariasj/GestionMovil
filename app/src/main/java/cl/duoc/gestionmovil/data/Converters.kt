package cl.duoc.gestionmovil.data

import androidx.room.TypeConverter
import cl.duoc.gestionmovil.model.*

class Converters {
    @TypeConverter
    fun fromRolUsuario(value: RolUsuario) = value.name
    @TypeConverter
    fun toRolUsuario(value: String) = enumValueOf<RolUsuario>(value)

    @TypeConverter
    fun fromTipoOportunidad(value: TipoOportunidad) = value.name
    @TypeConverter
    fun toTipoOportunidad(value: String) = enumValueOf<TipoOportunidad>(value)

    @TypeConverter
    fun fromPrioridad(value: Prioridad) = value.name
    @TypeConverter
    fun toPrioridad(value: String) = enumValueOf<Prioridad>(value)

    @TypeConverter
    fun fromPotencialComercial(value: PotencialComercial) = value.name
    @TypeConverter
    fun toPotencialComercial(value: String) = enumValueOf<PotencialComercial>(value)

    @TypeConverter
    fun fromEstadoOportunidad(value: EstadoOportunidad) = value.name
    @TypeConverter
    fun toEstadoOportunidad(value: String) = enumValueOf<EstadoOportunidad>(value)
}