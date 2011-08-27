import org.codehaus.groovy.runtime.ScriptBytecodeAdapter
import org.junit.After
import org.junit.Before
import static org.junit.Assert.fail

class MetaKoan {
    // TODO add it to Koan classes with an AST transformation?
    protected static final __ = 'fill_me_in'
    protected Map originalMetaClasses

    @Before
    void clearOriginalMetaClasses() {
        originalMetaClasses = [:]
    }

    @After
    void restoreOriginalMetaClasses() {
        originalMetaClasses.each { clazz, metaClass ->
            GroovySystem.metaClassRegistry.removeMetaClass(clazz)
            GroovySystem.metaClassRegistry.setMetaClass(clazz, metaClass)
        }
    }

    // Borrowed from Grails 1.3.7
    protected void storeOriginalMetaClass(Class clazz) {
        if (originalMetaClasses.containsKey(clazz)) return

        originalMetaClasses[clazz] = clazz.metaClass

        def emc = new ExpandoMetaClass(clazz, true, true)
        emc.initialize()
        GroovySystem.metaClassRegistry.setMetaClass(clazz, emc)
    }

    protected shouldFail = new GroovyTestCase().&shouldFail

    protected shouldNeverFail(Class clazz, Closure code) {
        Throwable th = null;
        try {
            code.call();
        } catch (GroovyRuntimeException gre) {
            th = ScriptBytecodeAdapter.unwrap(gre);
        } catch (Throwable e) {
            th = e;
        }

        if (clazz.isInstance(th)) {
            fail("It should have never failed with an exception of type " + clazz.getName());
        }
    }
}
