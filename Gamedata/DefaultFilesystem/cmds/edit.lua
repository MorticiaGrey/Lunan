if args[1] == nil then
    print("usage: edit [FILE]")
    return
end
local path = lunan.getSelectedPath() .. args[1]
if not lunan.openFileEditor(path) then
    lunan.makeFile(path)
    lunan.openFileEditor(path)
end
