# This recipe is for the i.MX fork of gstreamer1.0. For ease of
# maintenance, the top section is a verbatim copy of an OE-core
# recipe. The second section customizes the recipe for i.MX.

########### OE-core copy ##################
# Upstream hash: fb2d28e0315ece6180c87c7047587673024a09f7

SUMMARY = "GStreamer 1.0 multimedia framework"
DESCRIPTION = "GStreamer is a multimedia framework for encoding and decoding video and sound. \
It supports a wide range of formats including mp3, ogg, avi, mpeg and quicktime."
HOMEPAGE = "http://gstreamer.freedesktop.org/"
BUGTRACKER = "https://bugzilla.gnome.org/enter_bug.cgi?product=Gstreamer"
SECTION = "multimedia"
LICENSE = "LGPL-2.1-or-later"

DEPENDS = "glib-2.0 glib-2.0-native libxml2 bison-native flex-native"

inherit meson pkgconfig gettext upstream-version-is-even gobject-introspection ptest-gnome

LIC_FILES_CHKSUM = "file://COPYING;md5=69333daa044cb77e486cc36129f7a770 \
                    file://gst/gst.h;beginline=1;endline=21;md5=e059138481205ee2c6fc1c079c016d0d"

S = "${WORKDIR}/gstreamer-${PV}"

SRC_URI = "https://gstreamer.freedesktop.org/src/gstreamer/gstreamer-${PV}.tar.xz \
           file://run-ptest \
           file://0001-tests-respect-the-idententaion-used-in-meson.patch \
           file://0002-tests-add-support-for-install-the-tests.patch \
           file://0003-tests-use-a-dictionaries-for-environment.patch;striplevel=3 \
           file://0004-tests-add-helper-script-to-run-the-installed_tests.patch;striplevel=3 \
           "
SRC_URI[sha256sum] = "78d21b5469ac93edafc6d8ceb63bc82f6cbbee94d2f866cca6b9252157ee0a09"

PACKAGECONFIG ??= "${@bb.utils.contains('PTEST_ENABLED', '1', 'tests', '', d)} \
                   check \
                   debug \
                   tools"

PACKAGECONFIG[debug] = "-Dgst_debug=true,-Dgst_debug=false"
PACKAGECONFIG[tracer-hooks] = "-Dtracer_hooks=true,-Dtracer_hooks=false"
PACKAGECONFIG[coretracers] = "-Dcoretracers=enabled,-Dcoretracers=disabled"
PACKAGECONFIG[check] = "-Dcheck=enabled,-Dcheck=disabled"
PACKAGECONFIG[tests] = "-Dtests=enabled -Dinstalled_tests=true,-Dtests=disabled -Dinstalled_tests=false"
PACKAGECONFIG[unwind] = "-Dlibunwind=enabled,-Dlibunwind=disabled,libunwind"
PACKAGECONFIG[dw] = "-Dlibdw=enabled,-Dlibdw=disabled,elfutils"
PACKAGECONFIG[bash-completion] = "-Dbash-completion=enabled,-Dbash-completion=disabled,bash-completion"
PACKAGECONFIG[tools] = "-Dtools=enabled,-Dtools=disabled"
PACKAGECONFIG[setcap] = "-Dptp-helper-permissions=capabilities,,libcap libcap-native"

# TODO: put this in a gettext.bbclass patch
def gettext_oemeson(d):
    if d.getVar('USE_NLS') == 'no':
        return '-Dnls=disabled'
    # Remove the NLS bits if USE_NLS is no or INHIBIT_DEFAULT_DEPS is set
    if d.getVar('INHIBIT_DEFAULT_DEPS') and not oe.utils.inherits(d, 'cross-canadian'):
        return '-Dnls=disabled'
    return '-Dnls=enabled'

EXTRA_OEMESON += " \
    -Ddoc=disabled \
    -Dexamples=disabled \
    -Ddbghelp=disabled \
    ${@gettext_oemeson(d)} \
"

GIR_MESON_ENABLE_FLAG = "enabled"
GIR_MESON_DISABLE_FLAG = "disabled"

PACKAGES += "${PN}-bash-completion"

# Add the core element plugins to the main package
FILES:${PN} += "${libdir}/gstreamer-1.0/*.so"
FILES:${PN}-dev += "${libdir}/gstreamer-1.0/*.a ${libdir}/gstreamer-1.0/include"
FILES:${PN}-bash-completion += "${datadir}/bash-completion/completions/ ${datadir}/bash-completion/helpers/gst*"
FILES:${PN}-dbg += "${datadir}/gdb ${datadir}/gstreamer-1.0/gdb"

CVE_PRODUCT = "gstreamer"

PTEST_BUILD_HOST_FILES = ""

########### End of OE-core copy ###########

########### i.MX overrides ################

DEFAULT_PREFERENCE = "-1"

LIC_FILES_CHKSUM = " \
    file://LICENSE.txt;md5=69333daa044cb77e486cc36129f7a770 \
    file://gst/gst.h;beginline=1;endline=21;md5=e059138481205ee2c6fc1c079c016d0d \
"

# Use i.MX fork of GST for customizations
SRC_URI:remove = "https://gstreamer.freedesktop.org/src/gstreamer/gstreamer-${PV}.tar.xz"
SRC_URI:prepend = "${GST1.0_SRC};branch=${SRCBRANCH} "
GST1.0_SRC ?= "gitsm://github.com/nxp-imx/gstreamer.git;protocol=https"
SRCBRANCH = "MM_04.08.01_2308_L6.1.y"
SRCREV = "95112902507718085231a933cdfe54e3997d1b28"

S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "(imx-nxp-bsp)"

########### End of i.MX overrides #########
