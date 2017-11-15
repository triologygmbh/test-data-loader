import de.triology.blog.testdata.loader.testentities.InheritingEntity

create(InheritingEntity, "inherited") {
    inheritedField = "this Field is privately defined in the superclass"
    nonInheritedField = "this Field is privately defined in the the actual class"
}