import { cn } from '@/lib/utils'
import { ChevronDown } from 'lucide-react'
import {
  Children,
  isValidElement,
  useEffect,
  useId,
  useRef,
  useState,
  type ChangeEvent,
  type ReactNode,
} from 'react'

type SelectOption = {
  value: string
  label: string
  disabled?: boolean
}

function optionsFromChildren(children: ReactNode): SelectOption[] {
  return Children.toArray(children).flatMap((child) => {
    if (!isValidElement<{ value?: string | number; children?: ReactNode; disabled?: boolean }>(child)) {
      return []
    }
    if (child.type !== 'option') return []
    const value = String(child.props.value ?? '')
    const label = Children.toArray(child.props.children)
      .map((c) => (typeof c === 'string' || typeof c === 'number' ? String(c) : ''))
      .join('')
    return [{ value, label, disabled: child.props.disabled }]
  })
}

export interface SelectProps {
  id?: string
  name?: string
  value?: string | number
  defaultValue?: string | number
  disabled?: boolean
  required?: boolean
  className?: string
  children?: ReactNode
  onChange?: (event: ChangeEvent<HTMLSelectElement>) => void
}

export function Select({
  id,
  name,
  value,
  defaultValue,
  disabled,
  required,
  className,
  children,
  onChange,
}: SelectProps) {
  const autoId = useId()
  const listboxId = `${id ?? autoId}-listbox`
  const options = optionsFromChildren(children)
  const isControlled = value !== undefined
  const [internalValue, setInternalValue] = useState(
    String(defaultValue ?? options[0]?.value ?? ''),
  )
  const current = String(isControlled ? value : internalValue)
  const [open, setOpen] = useState(false)
  const rootRef = useRef<HTMLDivElement>(null)

  const selected = options.find((o) => o.value === current) ?? options[0]

  useEffect(() => {
    function onDocMouseDown(e: MouseEvent) {
      if (rootRef.current && !rootRef.current.contains(e.target as Node)) {
        setOpen(false)
      }
    }
    function onKey(e: KeyboardEvent) {
      if (e.key === 'Escape') setOpen(false)
    }
    document.addEventListener('mousedown', onDocMouseDown)
    document.addEventListener('keydown', onKey)
    return () => {
      document.removeEventListener('mousedown', onDocMouseDown)
      document.removeEventListener('keydown', onKey)
    }
  }, [])

  function commit(next: string) {
    if (!isControlled) setInternalValue(next)
    onChange?.({
      target: { value: next, name: name ?? '', id: id ?? '' },
      currentTarget: { value: next, name: name ?? '', id: id ?? '' },
    } as ChangeEvent<HTMLSelectElement>)
    setOpen(false)
  }

  return (
    <div ref={rootRef} className={cn('relative', className)}>
      {/* Keep a real select in the DOM for forms / autofill / a11y fallback */}
      <select
        name={name}
        value={current}
        required={required}
        disabled={disabled}
        tabIndex={-1}
        aria-hidden="true"
        className="pointer-events-none absolute h-0 w-0 opacity-0"
        onChange={() => undefined}
      >
        {options.map((o) => (
          <option key={o.value} value={o.value} disabled={o.disabled}>
            {o.label}
          </option>
        ))}
      </select>

      <button
        type="button"
        id={id}
        disabled={disabled}
        aria-haspopup="listbox"
        aria-expanded={open}
        aria-controls={listboxId}
        onClick={() => setOpen((v) => !v)}
        className={cn(
          'flex w-full items-center justify-between gap-2 rounded-[8px] border border-border bg-surface px-3 py-2.5 text-left text-sm text-ink outline-none transition-colors',
          'hover:border-ink-muted focus:border-accent disabled:cursor-not-allowed disabled:opacity-60',
          open && 'border-accent',
        )}
      >
        <span className={cn(!selected?.label && 'text-ink-muted')}>
          {selected?.label || 'Select…'}
        </span>
        <ChevronDown
          className={cn(
            'h-4 w-4 shrink-0 text-ink-muted transition-transform',
            open && 'rotate-180',
          )}
          aria-hidden
        />
      </button>

      {open ? (
        <ul
          id={listboxId}
          role="listbox"
          aria-activedescendant={
            selected ? `${listboxId}-opt-${selected.value || 'empty'}` : undefined
          }
          className="absolute left-0 right-0 top-full z-50 mt-1 max-h-56 overflow-auto rounded-[8px] border border-border bg-surface py-1 shadow-[0_8px_24px_rgba(44,44,42,0.12)]"
        >
          {options.map((o) => {
            const isSelected = o.value === current
            const optId = `${listboxId}-opt-${o.value || 'empty'}`
            return (
              <li
                key={o.value || '__empty'}
                id={optId}
                role="option"
                aria-selected={isSelected}
                aria-disabled={o.disabled || undefined}
                className={cn(
                  'cursor-pointer px-3 py-2 text-sm text-ink',
                  o.disabled && 'cursor-not-allowed opacity-50',
                  isSelected
                    ? 'bg-accent-soft text-accent-ink'
                    : 'hover:bg-avatar',
                )}
                onMouseDown={(e) => {
                  e.preventDefault()
                  if (o.disabled) return
                  commit(o.value)
                }}
              >
                {o.label}
              </li>
            )
          })}
        </ul>
      ) : null}
    </div>
  )
}
