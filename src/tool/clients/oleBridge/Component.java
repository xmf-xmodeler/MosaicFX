package tool.clients.oleBridge;

import java.util.Enumeration;
import java.util.Hashtable;

import xos.Value;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

// TODO: Auto-generated Javadoc
/**
 * The Class Component.
 */
public class Component {

	// call, invoke, get, getProperty, setProperty, put

	/** The id dispatch. */
	private static Hashtable<String,Object> idDispatch = new Hashtable<String,Object>();

	/**
	 * New top level.
	 *
	 * @param id the id
	 * @param target the target
	 */
	public static void newTopLevel(String id, String target) {
		ActiveXComponent ax = new ActiveXComponent(target);
		store(id, ax.getObject());
		Dispatch.put(ax, "Visible", true);
	}

	/**
	 * Call.
	 *
	 * @param targetId the target id
	 * @param call the call
	 * @param args the args
	 * @throws DispatchException the dispatch exception
	 */
	public static void call(String targetId, String call, Value[] args)
			throws DispatchException {
		if (idDispatch.containsKey(targetId)) {
			Object[] vars = valuesToVariants(args);
			Dispatch target = (Dispatch) idDispatch.get(targetId);
			if (vars.length == 0)
				Dispatch.call(target, call);
			if (vars.length == 1)
				Dispatch.call(target, call, vars[0]);
			if (vars.length == 2)
				Dispatch.call(target, call, vars[0], vars[1]);
			if (vars.length == 3)
				Dispatch.call(target, call, vars[0], vars[1], vars[2]);
			if (vars.length == 4)
				Dispatch.call(target, call, vars[0], vars[1], vars[3], vars[4]);
		} else {
			throw new DispatchException("No such dispatch: " + targetId);
		}
	}

	/**
	 * Call and store.
	 *
	 * @param targetId the target id
	 * @param storeId the store id
	 * @param call the call
	 * @param args the args
	 * @throws DispatchException the dispatch exception
	 */
	public static void callAndStore(String targetId, String storeId,
			String call, Value[] args) throws DispatchException {
		if (idDispatch.containsKey(targetId)) {
			Object[] vars = valuesToVariants(args);
			Dispatch target = (Dispatch) idDispatch.get(targetId);
			Dispatch d = null;
			if (vars.length == 0)
				d = Dispatch.call(target, call).toDispatch();
			if (vars.length == 1)
				d = Dispatch.call(target, call, vars[0]).toDispatch();
			if (vars.length == 2)
				d = Dispatch.call(target, call, vars[0], vars[1]).toDispatch();
			if (vars.length == 3)
				d = Dispatch.call(target, call, vars[0], vars[1], vars[2])
						.toDispatch();
			if (vars.length == 4)
				d = Dispatch.call(target, call, vars[0], vars[1], vars[3],
						vars[4]).toDispatch();
			store(storeId, d);
		} else {
			throw new DispatchException("No such dispatch: " + targetId);
		}
	}

	/**
	 * Gets the.
	 *
	 * @param targetId the target id
	 * @param property the property
	 * @return the value
	 * @throws DispatchException the dispatch exception
	 */
	public static Value get(String targetId, String property)
			throws DispatchException {
		if (idDispatch.containsKey(targetId)) {
			Object o = idDispatch.get(targetId);
			Variant v = Dispatch.get((Dispatch) o, property);
			return variantToValue(v);
		}
		throw new DispatchException("No such dispatch: " + targetId);
	}

	/**
	 * Gets the object.
	 *
	 * @param targetId the target id
	 * @param property the property
	 * @param id the id
	 * @return the object
	 * @throws DispatchException the dispatch exception
	 */
	public static void getObject(String targetId, String property, String id)
			throws DispatchException {
		if (idDispatch.containsKey(targetId)) {
			Object o = idDispatch.get(targetId);
			store(id, Dispatch.get((Dispatch) o, property).getDispatch());
		} else {
			String available = "";
			Enumeration<String> e = idDispatch.keys();
			while (e.hasMoreElements()) {
				available = available + e.nextElement() + " ";
			}
			throw new DispatchException("No such dispatch: " + targetId
					+ " available: " + available);
		}
	}

	/**
	 * Sets the.
	 *
	 * @param targetId the target id
	 * @param property the property
	 * @param value the value
	 * @throws DispatchException the dispatch exception
	 */
	public static void set(String targetId, String property, Value value)
			throws DispatchException {
		if (idDispatch.containsKey(targetId)) {
			Object o = idDispatch.get(targetId);
			switch (value.type) {
			case Value.STRING:
				Dispatch.put((Dispatch) o, property, value.strValue());
				break;
			case Value.INT:
				Dispatch.put((Dispatch) o, property,
						new Variant(value.intValue));
				break;
			case Value.BOOL:
				Dispatch.put((Dispatch) o, property, new Variant(
						value.boolValue));
				break;
			}
		} else {
			throw new DispatchException("No such dispatch: " + targetId);
		}
	}

	/**
	 * Sets the to dispatch.
	 *
	 * @param targetId the target id
	 * @param property the property
	 * @param id the id
	 * @throws DispatchException the dispatch exception
	 */
	public static void setToDispatch(String targetId, String property, String id)
			throws DispatchException {
		if (idDispatch.containsKey(targetId) && idDispatch.containsKey(id)) {
			Object o = idDispatch.get(targetId);
			Object v = idDispatch.get(id);
			Dispatch.put((Dispatch) o, property, v);
		} else {
			throw new DispatchException("No such dispatch: " + targetId);
		}
	}

	/**
	 * Store.
	 *
	 * @param storeid the storeid
	 * @param value the value
	 */
	public static void store(String storeid, Object value) {
		idDispatch.put(storeid, value);
	}

	/**
	 * Variant to value.
	 *
	 * @param v the v
	 * @return the value
	 */
	public static Value variantToValue(Variant v) {
		if (v.getvt() == Variant.VariantString)
			return new Value(v.getString());
		else if (v.getvt() == Variant.VariantInt)
			return new Value(v.getInt());
		else if (v.getvt() == Variant.VariantBoolean)
			return new Value(v.getBoolean());
		return null;
	}

	/**
	 * Value to variant.
	 *
	 * @param v the v
	 * @return the variant
	 */
	public static Variant valueToVariant(Value v) {
		if (v.type == Value.STRING)
			return new Variant(v.strValue());
		else if (v.type == Value.INT)
			return new Variant(v.intValue);
		else if (v.type == Value.BOOL)
			return new Variant(v.boolValue);
		return null;
	}

	/**
	 * Values to variants.
	 *
	 * @param values the values
	 * @return the object[]
	 */
	public static Object[] valuesToVariants(Value[] values) {
		Object[] objects = new Object[values.length];
		for (int i = 0; i < values.length; i++) {
			Variant v = valueToVariant(values[i]);
			objects[i] = v;
		}
		return objects;
	}

}
