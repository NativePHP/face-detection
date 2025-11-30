package com.nativephp.plugin.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.androidphp.bridge.BridgeFunction
import com.example.androidphp.bridge.BridgeResponse

object SensorsFunctions {

    // Cached sensor values
    private var accelerometerValues = floatArrayOf(0f, 0f, 0f)
    private var gyroscopeValues = floatArrayOf(0f, 0f, 0f)
    private var magnetometerValues = floatArrayOf(0f, 0f, 0f)
    private var rotationValues = floatArrayOf(0f, 0f, 0f, 0f, 0f)
    private var gravityValues = floatArrayOf(0f, 0f, 0f)
    private var pressureValue = 0f

    // Running state per sensor
    private var accelerometerRunning = false
    private var gyroscopeRunning = false
    private var magnetometerRunning = false
    private var deviceMotionRunning = false
    private var barometerRunning = false

    // Sensor availability
    private var hasAccelerometer = false
    private var hasGyroscope = false
    private var hasMagnetometer = false
    private var hasRotation = false
    private var hasGravity = false
    private var hasPressure = false

    private var sensorManager: SensorManager? = null
    private var initialized = false

    // Individual listeners for each sensor
    private val accelerometerListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            accelerometerValues = event.values.copyOf()
        }
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private val gyroscopeListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            gyroscopeValues = event.values.copyOf()
        }
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private val magnetometerListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            magnetometerValues = event.values.copyOf()
        }
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private val rotationListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            rotationValues = event.values.copyOf()
        }
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private val gravityListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            gravityValues = event.values.copyOf()
        }
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private val pressureListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            pressureValue = event.values[0]
        }
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private fun initialize(context: Context) {
        if (initialized) return

        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sm = sensorManager ?: return

        hasAccelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null
        hasGyroscope = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null
        hasMagnetometer = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null
        hasRotation = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null
        hasGravity = sm.getDefaultSensor(Sensor.TYPE_GRAVITY) != null
        hasPressure = sm.getDefaultSensor(Sensor.TYPE_PRESSURE) != null

        initialized = true
    }

    // MARK: - Accelerometer

    class AccelerometerStart(private val context: Context) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            initialize(context)

            if (accelerometerRunning) {
                return mapOf("success" to true, "message" to "Already running")
            }

            if (!hasAccelerometer) {
                return BridgeResponse.error("NOT_AVAILABLE", "Accelerometer not available")
            }

            val sm = sensorManager ?: return BridgeResponse.error("INIT_ERROR", "SensorManager not available")
            val sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            sm.registerListener(accelerometerListener, sensor, SensorManager.SENSOR_DELAY_GAME)
            accelerometerRunning = true

            return mapOf("success" to true)
        }
    }

    class AccelerometerStop(private val context: Context) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            initialize(context)

            if (!accelerometerRunning) {
                return mapOf("success" to true, "message" to "Not running")
            }

            sensorManager?.unregisterListener(accelerometerListener)
            accelerometerValues = floatArrayOf(0f, 0f, 0f)
            accelerometerRunning = false

            return mapOf("success" to true)
        }
    }

    class AccelerometerRead(private val context: Context) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            initialize(context)

            if (!accelerometerRunning) {
                return BridgeResponse.error("NOT_STARTED", "Accelerometer not started. Call start() first.")
            }

            return mapOf(
                "x" to accelerometerValues[0].toDouble(),
                "y" to accelerometerValues[1].toDouble(),
                "z" to accelerometerValues[2].toDouble()
            )
        }
    }

    // MARK: - Gyroscope

    class GyroscopeStart(private val context: Context) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            initialize(context)

            if (gyroscopeRunning) {
                return mapOf("success" to true, "message" to "Already running")
            }

            if (!hasGyroscope) {
                return BridgeResponse.error("NOT_AVAILABLE", "Gyroscope not available")
            }

            val sm = sensorManager ?: return BridgeResponse.error("INIT_ERROR", "SensorManager not available")
            val sensor = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
            sm.registerListener(gyroscopeListener, sensor, SensorManager.SENSOR_DELAY_GAME)
            gyroscopeRunning = true

            return mapOf("success" to true)
        }
    }

    class GyroscopeStop(private val context: Context) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            initialize(context)

            if (!gyroscopeRunning) {
                return mapOf("success" to true, "message" to "Not running")
            }

            sensorManager?.unregisterListener(gyroscopeListener)
            gyroscopeValues = floatArrayOf(0f, 0f, 0f)
            gyroscopeRunning = false

            return mapOf("success" to true)
        }
    }

    class GyroscopeRead(private val context: Context) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            initialize(context)

            if (!gyroscopeRunning) {
                return BridgeResponse.error("NOT_STARTED", "Gyroscope not started. Call start() first.")
            }

            return mapOf(
                "x" to gyroscopeValues[0].toDouble(),
                "y" to gyroscopeValues[1].toDouble(),
                "z" to gyroscopeValues[2].toDouble()
            )
        }
    }

    // MARK: - Magnetometer

    class MagnetometerStart(private val context: Context) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            initialize(context)

            if (magnetometerRunning) {
                return mapOf("success" to true, "message" to "Already running")
            }

            if (!hasMagnetometer) {
                return BridgeResponse.error("NOT_AVAILABLE", "Magnetometer not available")
            }

            val sm = sensorManager ?: return BridgeResponse.error("INIT_ERROR", "SensorManager not available")
            val sensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
            sm.registerListener(magnetometerListener, sensor, SensorManager.SENSOR_DELAY_GAME)
            magnetometerRunning = true

            return mapOf("success" to true)
        }
    }

    class MagnetometerStop(private val context: Context) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            initialize(context)

            if (!magnetometerRunning) {
                return mapOf("success" to true, "message" to "Not running")
            }

            sensorManager?.unregisterListener(magnetometerListener)
            magnetometerValues = floatArrayOf(0f, 0f, 0f)
            magnetometerRunning = false

            return mapOf("success" to true)
        }
    }

    class MagnetometerRead(private val context: Context) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            initialize(context)

            if (!magnetometerRunning) {
                return BridgeResponse.error("NOT_STARTED", "Magnetometer not started. Call start() first.")
            }

            return mapOf(
                "x" to magnetometerValues[0].toDouble(),
                "y" to magnetometerValues[1].toDouble(),
                "z" to magnetometerValues[2].toDouble()
            )
        }
    }

    // MARK: - Device Motion (rotation + gravity + gyroscope)

    class DeviceMotionStart(private val context: Context) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            initialize(context)

            if (deviceMotionRunning) {
                return mapOf("success" to true, "message" to "Already running")
            }

            if (!hasRotation && !hasGravity && !hasGyroscope) {
                return BridgeResponse.error("NOT_AVAILABLE", "Device motion sensors not available")
            }

            val sm = sensorManager ?: return BridgeResponse.error("INIT_ERROR", "SensorManager not available")

            if (hasRotation) {
                val sensor = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
                sm.registerListener(rotationListener, sensor, SensorManager.SENSOR_DELAY_GAME)
            }
            if (hasGravity) {
                val sensor = sm.getDefaultSensor(Sensor.TYPE_GRAVITY)
                sm.registerListener(gravityListener, sensor, SensorManager.SENSOR_DELAY_GAME)
            }
            if (hasGyroscope && !gyroscopeRunning) {
                // Only start gyroscope if not already running standalone
                val sensor = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
                sm.registerListener(gyroscopeListener, sensor, SensorManager.SENSOR_DELAY_GAME)
            }

            deviceMotionRunning = true
            return mapOf("success" to true)
        }
    }

    class DeviceMotionStop(private val context: Context) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            initialize(context)

            if (!deviceMotionRunning) {
                return mapOf("success" to true, "message" to "Not running")
            }

            sensorManager?.unregisterListener(rotationListener)
            sensorManager?.unregisterListener(gravityListener)
            // Only stop gyroscope if not running standalone
            if (!gyroscopeRunning) {
                sensorManager?.unregisterListener(gyroscopeListener)
            }

            rotationValues = floatArrayOf(0f, 0f, 0f, 0f, 0f)
            gravityValues = floatArrayOf(0f, 0f, 0f)
            deviceMotionRunning = false

            return mapOf("success" to true)
        }
    }

    class DeviceMotionRead(private val context: Context) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            initialize(context)

            if (!deviceMotionRunning) {
                return BridgeResponse.error("NOT_STARTED", "Device motion not started. Call start() first.")
            }

            // Convert rotation vector to euler angles
            val rotationMatrix = FloatArray(9)
            val orientation = FloatArray(3)

            if (hasRotation && rotationValues.size >= 4) {
                SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationValues)
                SensorManager.getOrientation(rotationMatrix, orientation)
            }

            return mapOf(
                "attitude" to mapOf(
                    "pitch" to orientation[1].toDouble(),
                    "roll" to orientation[2].toDouble(),
                    "yaw" to orientation[0].toDouble()
                ),
                "rotationRate" to mapOf(
                    "x" to gyroscopeValues[0].toDouble(),
                    "y" to gyroscopeValues[1].toDouble(),
                    "z" to gyroscopeValues[2].toDouble()
                ),
                "gravity" to mapOf(
                    "x" to gravityValues[0].toDouble(),
                    "y" to gravityValues[1].toDouble(),
                    "z" to gravityValues[2].toDouble()
                )
            )
        }
    }

    // MARK: - Barometer

    class BarometerStart(private val context: Context) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            initialize(context)

            if (barometerRunning) {
                return mapOf("success" to true, "message" to "Already running")
            }

            if (!hasPressure) {
                return BridgeResponse.error("NOT_AVAILABLE", "Barometer not available")
            }

            val sm = sensorManager ?: return BridgeResponse.error("INIT_ERROR", "SensorManager not available")
            val sensor = sm.getDefaultSensor(Sensor.TYPE_PRESSURE)
            sm.registerListener(pressureListener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            barometerRunning = true

            return mapOf("success" to true)
        }
    }

    class BarometerStop(private val context: Context) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            initialize(context)

            if (!barometerRunning) {
                return mapOf("success" to true, "message" to "Not running")
            }

            sensorManager?.unregisterListener(pressureListener)
            pressureValue = 0f
            barometerRunning = false

            return mapOf("success" to true)
        }
    }

    class BarometerRead(private val context: Context) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            initialize(context)

            if (!barometerRunning) {
                return BridgeResponse.error("NOT_STARTED", "Barometer not started. Call start() first.")
            }

            val altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressureValue)

            return mapOf(
                "pressure" to pressureValue.toDouble(),
                "altitude" to altitude.toDouble()
            )
        }
    }

    // MARK: - Availability

    class IsAvailable(private val context: Context) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            initialize(context)

            return mapOf(
                "accelerometer" to hasAccelerometer,
                "gyroscope" to hasGyroscope,
                "magnetometer" to hasMagnetometer,
                "deviceMotion" to (hasRotation || hasGravity || hasGyroscope),
                "barometer" to hasPressure
            )
        }
    }
}
