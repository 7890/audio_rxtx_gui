#!/bin/bash

#//tb/1410

cur=`pwd`

src="$cur"/src
build="$cur"/build
archive="$cur"/archive
classes="$build"/classes
doc="$cur"/doc

package_path=ch/lowres/audio_rxtx/gui

windows_binaries_zip_name=audio_rxtx_1416837145.zip
windows_binaries_uri="https://raw.githubusercontent.com/7890/jack_tools/master/audio_rxtx/dist/win/$windows_binaries_zip_name"

#dl to (incl. filename)
windows_binaries_zip="/tmp/$windows_binaries_zip_name"

#splash_screen_image="$src"/gfx/audio_rxtx_splash_screen.png
#icon_image="$src"/gfx/audio_rxtx_icon.png

#-Xlint:all 

function create_build_info()
{
	now="`date`"
	uname="`uname -m -o`"
	jvm="`javac -version 2>&1 | head -1 | sed 's/"/''/g'`"
	javac_opts=" -source 1.6 -target 1.6"

	cat - << __EOF__
//generated at build time
package ch.lowres.audio_rxtx.gui.helpers;
public class BuildInfo
{
	public static String get()
	{
		return "date: $now\nuname -m -o: $uname\njavac: $jvm\njavac options: $javac_opts";
	}
	public static void main(String[] args)
	{
		System.out.println(get());
	}
}
__EOF__
}

function compile_audio_rxtx()
{
	echo "building audio_rxtx gui application"
	echo "==================================="

	create_build_info > "$src"/$package_path/helpers/BuildInfo.java
	cat "$src"/$package_path/helpers/BuildInfo.java

	mkdir -p "$classes"

	javac -source 1.6 -target 1.6 -classpath "$classes" -sourcepath "$src" -d "$classes" "$src"/$package_path/*.java
	ret=$?
	if [ $ret -ne 0 ]
	then
		echo "error while compiling."
		exit 1
	fi

	echo "start with:"
	echo "java -splash:src/gfx/audio_rxtx_splash_screen.png -Xms1024m -Xmx1024m -cp .:build/classes/ ch.lowres.audio_rxtx.gui.Main"
}

function compile_java_osc
{
	echo "building JavaOSC library (com.illposed.osc)"
	echo "==========================================="
	cp "$archive"/JavaOSC-master.zip "$build"
	cd "$build"
	unzip JavaOSC-master.zip
	cd "$cur"

	cp "$archive"/JavaOSC_mod/OSCPort.java "$build"/JavaOSC-master/modules/core/src/main/java/com/illposed/osc/
	cp "$archive"/JavaOSC_mod/OSCPortOut.java "$build"/JavaOSC-master/modules/core/src/main/java/com/illposed/osc/

	PREF="$build"/JavaOSC-master/modules/core/src/main/java

	echo "compiling files in $PREF to direcotry $classes ..."

	mkdir -p "$classes"
	javac -source 1.6 -target 1.6 -classpath $PREF -sourcepath $PREF -d "$classes" $PREF/com/illposed/osc/*.java
	javac -source 1.6 -target 1.6 -classpath $PREF -sourcepath $PREF -d "$classes" $PREF/com/illposed/osc/utility/*.java
	find "$classes"
}

function compile_gettext
{
	echo "building gettext library (org.xnap.commons.i18n)"
	echo "================================================"
	cp "$archive"/gettext-commons-0.9.8-sources.jar "$build"
	cd "$build"
	jar xfv gettext-commons-0.9.8-sources.jar
	cd "$cur"

	PREF="$build"/

	echo "compiling files in $PREF to direcotry $classes ..."

	mkdir -p "$classes"
	javac -source 1.6 -target 1.6 -classpath $PREF -sourcepath $PREF -d "$classes" $PREF/org/xnap/commons/i18n/*.java
	find "$classes"
}

function create_languages
{
	xgettext -ktrc:1c,2 -ktrnc:1c,2,3 -ktr -kmarktr -ktrn:1,2 -o "$build"/keys.pot \
		"$src"/ch/lowres/audio_rxtx/gui/*.java \
		"$src"/ch/lowres/audio_rxtx/gui/api/*.java \
		"$src"/ch/lowres/audio_rxtx/gui/widgets/*.java \
		"$src"/ch/lowres/audio_rxtx/gui/osc/*.java

	#de
	touch "$build"/de.po
	cp "$src"/lang/de.po "$build"/de.po
	
	msgmerge -U "$build"/de.po "$build"/keys.pot

#	echo "edit $src/lang/de.po with poedit? y or enter"
#	read a
a=0

	if [ x"$a" = "xy" ]
	then
		echo "opening file $src/lang/de.po..."
		poedit "$src"/lang/de.po
	fi

	echo "creating i18n.Messages..."

	#de
	msgfmt --java2 -d "$classes" -r ch.lowres.audio_rxtx.gui.i18n.Messages -l de "$src"/lang/de.po
}

function build_jar
{
	echo "creating audio_rxtx application jar (audio_rxtx_gui_xxx.jar)"
	echo "============================================================"

	cur="`pwd`"

	mkdir -p "$classes"/resources
	cp "$archive"/AudioMono.ttf "$classes"/resources

	cp "$src"/gfx/audio_rxtx_icon.png "$classes"/resources
	cp "$src"/gfx/audio_rxtx_about_screen.png "$classes"/resources

	cp "$src"/etc/audio_rxtx_gui.properties "$classes"/resources

	##############################
	if [ ! -e "$windows_binaries_zip" ]
	then
		echo "curl -o $windows_binaries_zip $windows_binaries_uri"
		curl -o "$windows_binaries_zip" "$windows_binaries_uri"
	fi

	cp "$windows_binaries_zip" "$build"
	cd "$build"

	zip_filename="`echo \"$windows_binaries_zip\" | rev | cut -d\"/\" -f1 | rev`"
	unzip "$zip_filename"
	zip_dirname="`echo \"$zip_filename\" | rev | cut -d\".\" -f2- | rev`"
	cd "$zip_dirname"
	rm bin/*.bat
	rm bin/osc*.exe
	rm bin/audio_post_send.exe
	mkdir "$classes"/resources/win
	cp bin/* "$classes"/resources/win
	cp -r doc "$classes"/resources
	cp *.txt "$classes"/resources

	rm -rf audio_rxtx_*.zip

###################
	mkdir "$classes"/resources/mac

###################
	mkdir "$classes"/resources/lin32

###################
	mkdir "$classes"/resources/lin64

	echo "Main-Class: ch.lowres.audio_rxtx.gui.Main" > "$build"/Manifest.txt
	echo "" >> "$build"/Manifest.txt

	cd "$classes"

	now=`date +"%s"`

	echo "creating jar..."

	jar cfvm audio_rxtx_gui_"$now".jar "$build"/Manifest.txt ch/ com/ org/ resources/
	ls -l audio_rxtx_gui_"$now".jar
	echo "move audio_rxtx_gui_$now.jar to build dir..."
	mv audio_rxtx_gui_"$now".jar "$build"

	echo "start with"
	echo "java -splash:src/gfx/audio_rxtx_splash_screen.png -Xms1024m -Xmx1024m -jar build/audio_rxtx_gui_$now.jar"

	cd "$cur"
	java -splash:src/gfx/audio_rxtx_splash_screen.png -Xms1024m -Xmx1024m -jar build/audio_rxtx_gui_$now.jar

	echo "done."
}

function build_javadoc
{
	mkdir -p "$doc"
	javadoc -private -linksource -sourcetab 2 -d "$doc" \
	-classpath "$classes" \
	-sourcepath "$src" \
		ch.lowres.audio_rxtx.gui \
		ch.lowres.audio_rxtx.gui.widgets \
		ch.lowres.audio_rxtx.gui.helpers \
		ch.lowres.audio_rxtx.gui.api \
		ch.lowres.audio_rxtx.gui.osc
}

#execute:

mkdir -p "$build"
rm -rf "$build"/*

compile_java_osc
compile_gettext
create_languages
compile_audio_rxtx
#build_javadoc
build_jar
